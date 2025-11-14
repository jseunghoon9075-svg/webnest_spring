package com.app.webnest.api.publicapi;

import com.app.webnest.domain.dto.ApiResponseDTO;
import com.app.webnest.domain.dto.PostSearchDTO;
import com.app.webnest.domain.vo.QuizVO;
import com.app.webnest.domain.vo.UserVO;
import com.app.webnest.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:3000")  // 추가
@RestController
@RequiredArgsConstructor
@RequestMapping("/search")
@Slf4j
public class SearchApi {
    private final SearchService searchService;

    @GetMapping
    public ResponseEntity<ApiResponseDTO> searchResult(@RequestParam(value = "search", required = false) String[] queries) {
        // 배열이 비어있거나 null이면 빈 결과 반환
        if (queries == null || queries.length == 0) {
            Map<String, Object> emptyResult = new HashMap<>();
            emptyResult.put("search", queries);
            emptyResult.put("openPosts", new ArrayList<>());
            emptyResult.put("questionPosts", new ArrayList<>());
            emptyResult.put("quizzes", new ArrayList<>());
            emptyResult.put("users", new ArrayList<>());
            return ResponseEntity.ok(ApiResponseDTO.of("검색 결과", emptyResult));
        }

        Map<String, Object> result = new HashMap<>();
        List<PostSearchDTO> openPosts = new ArrayList<>();
        List<PostSearchDTO> questionPosts = new ArrayList<>();
        List<QuizVO> quizzes = new ArrayList<>();
        List<UserVO> users = new ArrayList<>();

        // 배열의 각 검색어로 검색하고 결과 합치기 (중복 제거)
        for (String query : queries) {
            if (query != null && !query.trim().isEmpty()) {
                openPosts.addAll(searchService.getOpenPostBySearchQuery(query));
                questionPosts.addAll(searchService.getQuestionPostBySearchQuery(query));
                quizzes.addAll(searchService.getQuizBySearchQuery(query));
                users.addAll(searchService.getUserBySearchQuery(query));
            }
        }

        // 중복 제거 (ID 기준으로 Set 사용)
        Set<PostSearchDTO> openPostsSet = new LinkedHashSet<>(openPosts);
        Set<PostSearchDTO> questionPostsSet = new LinkedHashSet<>(questionPosts);
        Set<QuizVO> quizzesSet = new LinkedHashSet<>(quizzes);
        Set<UserVO> usersSet = new LinkedHashSet<>(users);
        
        // ID 기준으로 중복 제거 (equals/hashCode가 제대로 구현되어 있지 않을 수 있으므로)
        users = users.stream()
                .collect(Collectors.toMap(
                    UserVO::getId,
                    user -> user,
                    (existing, replacement) -> existing
                ))
                .values()
                .stream()
                .collect(Collectors.toList());
        
        openPosts = new ArrayList<>(openPostsSet);
        questionPosts = new ArrayList<>(questionPostsSet);
        quizzes = new ArrayList<>(quizzesSet);

        result.put("search", queries);
        result.put("openPosts", openPosts);
        result.put("questionPosts", questionPosts);
        result.put("quizzes", quizzes);
        result.put("users", users);

        return ResponseEntity.ok(ApiResponseDTO.of("검색 결과", result));
    }
}
