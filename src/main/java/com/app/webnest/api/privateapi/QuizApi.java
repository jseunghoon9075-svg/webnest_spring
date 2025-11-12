package com.app.webnest.api.privateapi;

import com.app.webnest.domain.dto.ApiResponseDTO;
import com.app.webnest.domain.dto.QuizPersonalDTO;
import com.app.webnest.domain.dto.QuizResponseDTO;
import com.app.webnest.domain.vo.QuizVO;
import com.app.webnest.exception.GlobalExceptionHandler;
import com.app.webnest.exception.QuizException;
import com.app.webnest.service.JavaCompileService;
import com.app.webnest.service.QuizService;
import com.app.webnest.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/*")
@RequiredArgsConstructor
@Slf4j
public class QuizApi {

    private final JavaCompileService javaCompileService;
    private final QuizService quizService;
    private final UserService userService;

    @PostMapping("/quiz")
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> getQuizList(@RequestBody(required = false) Map<String,Object> params) {
        // 쿼리에 넘길 Map 구성

        if (params == null) params = new HashMap<>();
//        전체 문제 수
        // 안전한 파싱 및 기본값
        String quizLanguage = params.get("quizLanguage") == null ? null : String.valueOf(params.get("quizLanguage"));
        String quizDifficult = params.get("quizDifficult") == null ? null : String.valueOf(params.get("quizDifficult"));
        String keyword = params.get("keyword") == null ? null : String.valueOf(params.get("keyword"));

        int page = 1;
        if (params.get("page") != null) {
            try {
                page = Integer.parseInt(String.valueOf(params.get("page"))); // 들어오는 현재페이지번호 ex) Object타입의 "1" String으로 형변환후 Integer로 다시 형변환해서 처리
                if (page < 1) page = 1;
            } catch (NumberFormatException ignored) {}
        }

//                 화면에서 받아올 값
        HashMap<String, Object> filters = new HashMap<>();
        filters.put("quizLanguage", quizLanguage);
        filters.put("quizDifficult", quizDifficult);
        filters.put("keyword", keyword);
        filters.put("page", page); // 매퍼에서 page로 OFFSET 계산

        List<QuizVO> findQuizList = quizService.quizDirection(filters); // service에서 매퍼 호출
        if (findQuizList == null) findQuizList = new ArrayList<>();
        Long quizTotalCount = quizService.quizCount(filters);
        Map<String,Object> data = new HashMap<>();
        data.put("findQuizList", findQuizList);
        data.put("quizTotalCount", quizTotalCount);

        return ResponseEntity.ok(ApiResponseDTO.of("문제리스트 불러오기", data));

    };

    @GetMapping("/workspace/quiz/{id}")
    public ResponseEntity<ApiResponseDTO<HashMap>> getQuizById(@PathVariable("id") Long id) {
        HashMap <String, Object> quizDatas = new HashMap<>();
        QuizVO findQuiz = quizService.findQuizById(id);
        QuizPersonalDTO quizPersonalData = quizService.findQuizPersonalByAll();

        quizDatas.put("findQuiz", findQuiz);
        quizDatas.put("findQuizPersonalData", quizPersonalData);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponseDTO.of("문제상세조회",  quizDatas));
    }

    @PostMapping("/quiz/all")
    public ResponseEntity<ApiResponseDTO<List<QuizVO>>> getAllQuizList() {
        List<QuizVO>quizList = quizService.quizList();
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponseDTO.of("전체문제조회", quizList));
    }

    @PostMapping("/quiz/java-expectation")
    public ResponseEntity<ApiResponseDTO<String>> getJavaExpectation(@RequestBody QuizResponseDTO quizResponseDTO ) {
        String code = quizResponseDTO.getCode();
        Long id = quizResponseDTO.getQuizId();
        String className = quizResponseDTO.getClassName();
        String foundQuizExpectation = quizService.findQuizExpectationById(id);
        if(code == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponseDTO.of("잘못된 요청"));
        }
        String result = javaCompileService.execute(className, code);

        if(result == null) {
            throw new QuizException("컴파일오류");
        }
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponseDTO.of("실행 성공", result));
    }

    @PostMapping("/quiz/sql-expectation")
    public ResponseEntity<ApiResponseDTO<String>> getSqlExpectation(@RequestBody QuizResponseDTO quizResponseDTO ) {
        Long findQuizId = quizResponseDTO.getQuizId();

        log.info("quizId: {}", findQuizId);
        String getUserCode = quizResponseDTO.getCode();
        log.info("userCode: {}", getUserCode);
        String quizExpectation = quizService.findQuizExpectationById(findQuizId).toUpperCase();

        if(!getUserCode.equals(quizExpectation)) {
           throw new QuizException("잘못된 쿼리");
        }
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponseDTO.of("실행 성공", quizExpectation));
    }

    @PostMapping("/quiz/java-success")
    public ResponseEntity<ApiResponseDTO<HashMap>> getIsSuccess(@RequestBody QuizResponseDTO quizResponseDTO ) {
        HashMap<String, Object> data = new HashMap<>();
        String code = quizResponseDTO.getCode();
        String className = quizResponseDTO.getClassName();

        Long userId = quizResponseDTO.getUserId();
        boolean isSolve = quizResponseDTO.isSolve();
        boolean isBookmark = quizResponseDTO.isBookmark();

        log.info("code: {}", code);
        log.info("userId: {}", userId);
        log.info("isSolve: {}", isSolve);
        log.info("isBookmark: {}", isBookmark);
//        화면에 반환할것 - 해당문제 경험치, 정답여부

        Long findQuizId = quizResponseDTO.getQuizId();
        QuizVO findQuiz = quizService.findQuizById(findQuizId);
        String findQuizExpectation = findQuiz.getQuizExpectation();
        Integer findExp = findQuiz.getQuizExp();

        String result = javaCompileService.execute(className, code);

        if(result == null) {
            throw new QuizException("소스코드를 다시 확인해주세요");
        }

        if(!result.equals(findQuizExpectation)){
            throw new QuizException("기댓값과 일치하지 않습니다. 다시 시도해보세요!");
        }
//        화면에서 유저 아이디를 받고 저장돼있는 기댓값과 같으면 해당 퀴즈의 exp를 받아서 유저 exp 업데이트 쿼리전송
        userService.gainExp(userId, findExp);


        return ResponseEntity.status(HttpStatus.OK).body(ApiResponseDTO.of("기댓값과 일치합니다!", data));
    }

    @PostMapping("/quiz/sql-success")
    public ResponseEntity<ApiResponseDTO<HashMap>> getSqlSuccess(@RequestBody QuizResponseDTO quizResponseDTO ) {
        HashMap<String, Object> data = new HashMap<>();
        Long findQuizId = quizResponseDTO.getQuizId();

        String code = quizResponseDTO.getCode();

        String quizExpectation = quizService.findQuizExpectationById(findQuizId).toUpperCase();
        QuizVO findQuiz = quizService.findQuizById(findQuizId);
        String findQuizExp = String.valueOf(findQuiz.getQuizExp());
        if(!code.equals(quizExpectation)) {
            throw new QuizException("잘못된 쿼리 ex) 소문자입력, 세미콜론 미작성");
        }
        data.put("QuizExp", findQuizExp);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponseDTO.of("기댓값과 일치합니다!", data));
    }

}
