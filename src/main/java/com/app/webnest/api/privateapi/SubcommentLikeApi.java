package com.app.webnest.api.privateapi;

import com.app.webnest.domain.dto.ApiResponseDTO;
import com.app.webnest.domain.vo.SubcommentLikeVO;
import com.app.webnest.service.CommentLikeService;
import com.app.webnest.service.SubcommentLikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/subcommentLike")
public class SubcommentLikeApi {

    private final SubcommentLikeService subcommentLikeService;

    @GetMapping("/{subcommentId}")
    public ResponseEntity<ApiResponseDTO> getSubcommentLikeCount(@PathVariable("subcommentId") Long subcommentId) {
        int likeCount = subcommentLikeService.getSubcommentLike(subcommentId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDTO.of("댓글 좋아요 수 조회 성공", likeCount));
    }


    @PostMapping("/write")
    public ResponseEntity<ApiResponseDTO> write(@RequestBody SubcommentLikeVO subcommentLikeVO) {
        Map<String, Long> response = subcommentLikeService.save(subcommentLikeVO);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDTO.of("게시글 작성 완료", response));
    }
}
