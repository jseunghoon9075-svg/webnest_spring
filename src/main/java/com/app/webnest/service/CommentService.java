package com.app.webnest.service;

import com.app.webnest.domain.dto.CommentDTO;
import com.app.webnest.domain.vo.CommentVO;
import com.app.webnest.domain.vo.PostVO;

import java.util.List;
import java.util.Map;

public interface CommentService {
    //게시글에서 댓글 읽기
    public List<CommentDTO> getCommentsByPostId(Long postId);

    //답글 작성
    public Map<String, Long> writeComment(CommentVO commentVO);
}
