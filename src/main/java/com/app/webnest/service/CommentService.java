package com.app.webnest.service;

import com.app.webnest.domain.dto.CommentDTO;

import java.util.List;

public interface CommentService {
    //게시글에서 댓글 읽기
    public List<CommentDTO> getCommentsByPostId(Long postId);

}
