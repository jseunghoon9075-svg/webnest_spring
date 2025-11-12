package com.app.webnest.api.privateapi;


import com.app.webnest.domain.dto.ApiResponseDTO;
import com.app.webnest.domain.dto.CommentDTO;
import com.app.webnest.domain.dto.PostResponseDTO;
import com.app.webnest.domain.vo.CommentVO;
import com.app.webnest.domain.vo.PostVO;
import com.app.webnest.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentApi {
    private final CommentService commentService;

    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponseDTO> getPost(@PathVariable Long postId) {
        List<CommentDTO> comments = commentService.getCommentsByPostId(postId);
        return  ResponseEntity.status(HttpStatus.OK).body(ApiResponseDTO.of("게시글 조회 성공", comments));
    }

    //답글 작성
    @PostMapping("/write")
    public ResponseEntity<ApiResponseDTO> writeComments(@RequestBody CommentVO commentVO) {
        Map<String, Long> response = commentService.writeComment(commentVO);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDTO.of("게시글 작성 완료", response));
    }
}


