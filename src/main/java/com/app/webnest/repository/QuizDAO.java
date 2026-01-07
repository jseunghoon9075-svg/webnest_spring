package com.app.webnest.repository;

import com.app.webnest.domain.dto.QuizMyPageDTO;
import com.app.webnest.domain.dto.QuizPersonalDTO;
import com.app.webnest.domain.dto.QuizPersonalResponseDTO;
import com.app.webnest.domain.dto.QuizResponseDTO;
import com.app.webnest.domain.vo.QuizPersonalVO;
import com.app.webnest.domain.vo.QuizSubmitVO;
import com.app.webnest.domain.vo.QuizVO;
import com.app.webnest.mapper.QuizMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class QuizDAO {

    private final QuizMapper quizMapper;

//    페이징, 필터링
    public List<QuizVO> findQuizAll(HashMap<String, Object> params){
        return quizMapper.selectAllFilter(params);
    }


    public List<QuizPersonalDTO> findQuizWithPersonal(HashMap<String, Object> params){ return quizMapper.selectQuizWithPersonal(params); }

//    전체 문제리스트
    public List<QuizVO> findQuizAll(){ return  quizMapper.selectAll(); }

//    전체 문제수
    public Long findAllCount(HashMap<String, Object> filters){ return quizMapper.selectListTotalCount(filters); }

//    문제 조회
    public QuizVO findById(Long id) { return quizMapper.select(id); }

//    문제 기댓값
    public String findExpectationById(Long quizId) { return quizMapper.selectExpectation(quizId); }

//    퀴즈리드 (join)
    public QuizPersonalDTO findQuizPersonalAll() { return  quizMapper.selectQuizPersonalAll(); }

    //    해당퀴즈에 대한 personal정보
    public Long  findQuizPersonalById(QuizResponseDTO quizResponseDTO) { return quizMapper.selectQuizPersonalById(quizResponseDTO); }


    public QuizPersonalVO findAllQuizPersonalById(Long id)  { return quizMapper.selectAllQuizPersonalById(id); }

    //    퀴즈 풀었던 내역저장
    public void insertQuizPersonal(QuizPersonalVO quizPersonalVO) { quizMapper.insert(quizPersonalVO); }

//    북마크여부 업데이트
    public Integer updateIsBookmark(QuizResponseDTO quizResponseDTO){ return quizMapper.updateIsBookmark(quizResponseDTO); }

//    해결여부 업데이트
    public boolean updateIsSolve(QuizResponseDTO quizResponseDTO){ return quizMapper.updateIsSolve(quizResponseDTO); }

//    북마크, 해결목록 목록 반환
    public List<QuizPersonalResponseDTO> findByBookmarkIsSolve(Long userId){ return quizMapper.selectByBookmarkIsSolve(userId);}

//    회원탈퇴시 데이터삭제
    public void deleteQuizPersonal(Long id){}

    //    퀴즈 제출내역 추가
    public void insertByQuizSubmit(QuizResponseDTO quizResponseDTO) { quizMapper.insertQuizSubmit(quizResponseDTO);}

    //    한사람의 해당문제에 대한 제출내역
    public QuizSubmitVO findByQuizSubmit(QuizResponseDTO quizResponseDTO) { return quizMapper.selectQuizSubmit(quizResponseDTO); }

    //    한사람의 모든문제에 대한 제출내역들
    public List<QuizSubmitVO> findByQuizSubmitAll(QuizResponseDTO quizResponseDTO) {return quizMapper.selectQuizSubmitAll(quizResponseDTO); }

    //    채점 후 정답이면 정답여부 업데이트
    public void updateBySubmitResult(QuizResponseDTO quizResponseDTO) { quizMapper.updateSubmitResult(quizResponseDTO); }


//    마이페이지 내가 푼 문제
    public List<QuizMyPageDTO> findByIdQuizIsSolveMyData(Long userId){ return quizMapper.selectQuizIsSolveMyData(userId);}

//    마이페이지 내가 푼 문제의 언어들
    public List<QuizMyPageDTO> findByIdQuizIsSolveForLanguageMyData(Long userId){ return quizMapper.selectQuizIsSolveForLanguageMyData(userId);}
}
