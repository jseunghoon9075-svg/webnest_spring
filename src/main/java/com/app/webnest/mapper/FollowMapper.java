package com.app.webnest.mapper;

import com.app.webnest.domain.dto.FollowDTO;
import com.app.webnest.domain.vo.FollowVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FollowMapper {
    // 특정 유저가 팔로잉하는 유저들 조회
    // userId가 팔로잉하는 사람들 = followerId = userId인 경우의 userId들
    public List<FollowDTO> selectFollowingByUserId(Long userId);
    
    // 특정 유저를 팔로우하는 유저들 조회 (팔로워 리스트)
    // userId를 팔로우하는 사람들 = userId = userId인 경우의 followerId들
    public List<FollowDTO> selectFollowersByUserId(Long userId);
}

