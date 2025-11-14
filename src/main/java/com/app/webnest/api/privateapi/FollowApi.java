package com.app.webnest.api.privateapi;

import com.app.webnest.domain.dto.ApiResponseDTO;
import com.app.webnest.domain.dto.FollowDTO;
import com.app.webnest.service.FollowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/private/follows")
@Slf4j
public class FollowApi {

    private final FollowService followService;

    /**
     * 특정 유저가 팔로잉하는 유저들 조회
     * GET /private/follows/{userId}/following
     */
    @GetMapping("/{userId}/following")
    public ResponseEntity<ApiResponseDTO<List<FollowDTO>>> getFollowing(@PathVariable Long userId) {
        List<FollowDTO> following = followService.getFollowingByUserId(userId);
        log.info("Following following request received");
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponseDTO.of("팔로잉 리스트 조회 성공", following));
    }

    /**
     * 특정 유저를 팔로우하는 유저들 조회 (팔로워 리스트)
     * GET /private/follows/{userId}/followers
     */
    @GetMapping("/{userId}/followers")
    public ResponseEntity<ApiResponseDTO<List<FollowDTO>>> getFollowers(@PathVariable Long userId) {
        List<FollowDTO> followers = followService.getFollowersByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponseDTO.of("팔로워 리스트 조회 성공", followers));
    }
}

