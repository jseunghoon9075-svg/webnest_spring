package com.app.webnest.service;

import com.app.webnest.repository.CommentLikeDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentLikeServiceImpl implements CommentLikeService {
    private final CommentLikeDAO commentLikeDAO;

    @Override
    public int getCommentLike(Long commentId) {
        return commentLikeDAO.findCommentLike(commentId);
    }


    @Override
    public Map<String, Object> toggleLike(Long commentId, Long postId, Long userId) {

        boolean isLiked = commentLikeDAO.isLiked(commentId, userId);

        if (isLiked) {
            commentLikeDAO.removeLike(commentId, userId);
        } else {
            commentLikeDAO.addLike(commentId, postId, userId);
        }

        int likeCount = commentLikeDAO.countLike(commentId);

        Map<String, Object> result = new HashMap<>();
        result.put("liked", !isLiked);      // 변경된 상태
        result.put("likeCount", likeCount); // 최신 좋아요 개수

        return result;
    }
}
