package com.app.webnest.repository;

import com.app.webnest.domain.dto.FollowDTO;
import com.app.webnest.mapper.FollowMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class FollowDAO {
    private final FollowMapper followMapper;

    // 특정 유저가 팔로잉하는 유저들 조회
    public List<FollowDTO> findFollowingByUserId(Long userId) {
        return followMapper.selectFollowingByUserId(userId);
    }

    // 특정 유저를 팔로우하는 유저들 조회 (팔로워 리스트)
    public List<FollowDTO> findFollowersByUserId(Long userId) {
        return followMapper.selectFollowersByUserId(userId);
    }
}

