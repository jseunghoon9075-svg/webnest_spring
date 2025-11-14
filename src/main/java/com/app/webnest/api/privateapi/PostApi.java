package com.app.webnest.api.privateapi;

import com.app.webnest.domain.dto.ApiResponseDTO;
import com.app.webnest.domain.dto.PostResponseDTO;
import com.app.webnest.domain.vo.PostVO;
import com.app.webnest.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostApi {

    private final PostService postService;

//    @PostMapping("write")
//    public ResponseEntity<ApiResponseDTO> writePost(@RequestBody PostVO postVO) {
//        Map<String, Long> response = postService.write(postVO);
//        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDTO.of("게시글 작성 완료", response));
//    }

    // 열린둥지 전체 조회
    @GetMapping("/open")
    public List<PostResponseDTO> getOpenPosts() {
        return postService.getOpenPosts();
    }

    // 문제둥지 전체 조회
//    @GetMapping("/question")
//    public List<PostResponseDTO> getQuestionPosts() {
//        return postService.getQuestionPosts();
//
//    }
    // 문제둥지 전체 조회
    @GetMapping("/question")
    public List<PostResponseDTO> getQuestionPosts() {
        List<PostResponseDTO> posts = postService.getQuestionPosts(); // ✅ 리스트 선언
        System.out.println("🔥 게시글 개수: " + posts.size()); // ✅ size() 찍기
        return posts; // ✅ 그대로 반환
    }


//    // 상세 조회
//    @GetMapping("get-post/{id}")
//    public ResponseEntity<ApiResponseDTO> getPost(@PathVariable Long id) {
//        PostResponseDTO post = postService.getPost(id);
//        return ResponseEntity.status(HttpStatus.OK).body(ApiResponseDTO.of("게시글 조회 성공", post));
//    }
//
//    //조회수 증가 안됨
//    @GetMapping("/get-post-no-view/{id}")
//    public ResponseEntity<ApiResponseDTO> getPostNoView(@PathVariable Long id) {
//        PostResponseDTO post = postService.getPostWithoutView(id);
//        return ResponseEntity.status(HttpStatus.OK)
//                .body(ApiResponseDTO.of("조회수 증가 없이 조회", post));
//    }
    // 조회수 증가 O
    @GetMapping("get-post/{id}")
    public ResponseEntity<ApiResponseDTO> getPost(
            @PathVariable Long id,
            @RequestParam Long userId
    ){
        PostResponseDTO post = postService.getPost(id, userId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponseDTO.of("게시글 조회 성공", post));
    }

    // 조회수 증가 X
    @GetMapping("/get-post-no-view/{id}")
    public ResponseEntity<ApiResponseDTO> getPostNoView(
            @PathVariable Long id,
            @RequestParam Long userId
    ){
        PostResponseDTO post = postService.getPostWithoutView(id, userId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponseDTO.of("조회수 증가 없이 조회", post));
    }

    // 마이페이지 - 열린둥지 전체
    @GetMapping("/users/{userId}/open")
    public List<PostResponseDTO> getMyOpenPosts(@PathVariable Long userId){
        return postService.getOpenPostsByUserId(userId);
    }

    // 마이페이지 - 문제둥지 전체
    @GetMapping("/users/{userId}/question")
    public List<PostResponseDTO> getMyQuestionPosts(@PathVariable Long userId){
        return postService.getQuestionPostsByUserId(userId);
    }

//    @PutMapping("modify")
//    public ResponseEntity<ApiResponseDTO> updatePost(@RequestBody PostVO postVO) {
//        postService.modify(postVO);
//        return ResponseEntity.status(HttpStatus.OK).body(ApiResponseDTO.of("게시글 수정 조회 성공"));
//    }
//
//    @DeleteMapping("remove")
//    public ResponseEntity<ApiResponseDTO> updatePost(@RequestBody Long id) {
//        postService.remove(id);
//        return ResponseEntity.status(HttpStatus.OK).body(ApiResponseDTO.of("게시글 삭제 성공"));
//    }

    //게시글 작성
    @PostMapping("/write")
    public ResponseEntity<ApiResponseDTO> writePost(@RequestBody PostVO postVO) {
        Map<String, Long> response = postService.write(postVO);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDTO.of("게시글 작성 완료", response));
    }




    @PostMapping("/like")
    public ResponseEntity<ApiResponseDTO> toggleLike(
            @RequestParam Long postId,
            @RequestParam Long userId
    ) {
        Map<String, Object> result = postService.togglePostLike(postId, userId);
        return ResponseEntity.ok(ApiResponseDTO.of("좋아요 변경 완료", result));
    }




}
