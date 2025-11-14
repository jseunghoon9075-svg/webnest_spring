package com.app.webnest.service;

import java.util.Map;

public interface CommentLikeService {

    public int getCommentLike(Long commentId);


    public Map<String, Object> toggleLike(Long commentId, Long postId, Long userId);
}
