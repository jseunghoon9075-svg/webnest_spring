package com.app.webnest.api.privateapi;


import com.app.webnest.domain.dto.ApiResponseDTO;
import com.app.webnest.domain.dto.CommentDTO;
import com.app.webnest.domain.dto.PostResponseDTO;
import com.app.webnest.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
}


