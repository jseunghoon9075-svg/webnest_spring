package com.app.webnest.api.privateapi;

import com.app.webnest.domain.dto.ApiResponseDTO;
import com.app.webnest.domain.dto.PostResponseDTO;
import com.app.webnest.domain.vo.UserVO;
import com.app.webnest.service.PostService;
import com.app.webnest.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/private/my-page/*")
public class MyPageApi {

    private final PostService postService;
    private final UserService userService;

    @PostMapping("private-test")
    public void privateTest(Authentication authentication){
        log.info(authentication.getPrincipal().toString());
    }
    // 퀴즈 내가 푼 문제 + 필터 가능



    // 마이페이지 - 열린둥지 전체 ( 내가 쓴 )
    @PostMapping("{userId}/open")
    public List<PostResponseDTO> getMyOpenPosts(@PathVariable Long userId){
        return postService.getOpenPostsByUserId(userId);
    }
    // 마이페이지 - 문제둥지 전체 ( 내가 쓴 )
    @PostMapping("{userId}/question")
    public List<PostResponseDTO> getMyQuestionPosts(@PathVariable Long userId){
        return postService.getQuestionPostsByUserId(userId);
    }

    // 마이페이지 - 열린둥지 전체 ( 좋아요 누른 )


    // 마이페이지 - 문제 둥지 전체 ( 좋아요 누른 )


    // 팔로우


    // 회원수정
    @PutMapping("/modify")
    public ResponseEntity<ApiResponseDTO> modify(@RequestBody UserVO userVO){
        userService.modify(userVO);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponseDTO.of("정보 수정이 완료되었습니다.")); // 200
    }




}
