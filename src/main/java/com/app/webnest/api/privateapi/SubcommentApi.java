package com.app.webnest.api.privateapi;

import com.app.webnest.domain.dto.ApiResponseDTO;
import com.app.webnest.domain.dto.SubcommentDTO;
import com.app.webnest.service.SubcommentService;
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
@RequestMapping("/subcomment")
public class SubcommentApi {
    private final SubcommentService subcommentService;

    // GET /subcomment/get-comments/{commentId}
    @GetMapping("/get-comments/{commentId}")
    public ResponseEntity<ApiResponseDTO> getSubcomments(@PathVariable("commentId") Long commentId) {
        List<SubcommentDTO> subcomments = subcommentService.getSubcomments(commentId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDTO.of("대댓글 조회 성공", subcomments));
    }
}
