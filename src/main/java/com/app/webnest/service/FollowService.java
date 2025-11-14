package com.app.webnest.service;

import com.app.webnest.domain.dto.FollowDTO;

import java.util.List;

public interface FollowService {
    // 특정 유저가 팔로잉하는 유저들 조회
    public List<FollowDTO> getFollowingByUserId(Long userId);
    
    // 특정 유저를 팔로우하는 유저들 조회 (팔로워 리스트)
    public List<FollowDTO> getFollowersByUserId(Long userId);
}

