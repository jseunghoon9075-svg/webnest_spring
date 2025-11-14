package com.app.webnest.service;

import com.app.webnest.domain.dto.FollowDTO;
import com.app.webnest.repository.FollowDAO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class FollowServiceImpl implements FollowService {
    private final FollowDAO followDAO;

    @Override
    public List<FollowDTO> getFollowingByUserId(Long userId) {
        try {
            List<FollowDTO> following = followDAO.findFollowingByUserId(userId);
            if (following == null || following.isEmpty()) {
                return new ArrayList<>();
            }
            return following;
        } catch (Exception e) {
            log.error("팔로잉 리스트 조회 실패. userId: {}, error: {}", userId, e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public List<FollowDTO> getFollowersByUserId(Long userId) {
        try {
            List<FollowDTO> followers = followDAO.findFollowersByUserId(userId);
            if (followers == null || followers.isEmpty()) {
                return new ArrayList<>();
            }
            return followers;
        } catch (Exception e) {
            log.error("팔로워 리스트 조회 실패. userId: {}, error: {}", userId, e.getMessage());
            return new ArrayList<>();
        }
    }
}

