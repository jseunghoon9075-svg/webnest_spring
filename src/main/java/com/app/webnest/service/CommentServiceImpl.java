package com.app.webnest.service;

import com.app.webnest.domain.dto.CommentDTO;
import com.app.webnest.domain.vo.CommentVO;
import com.app.webnest.domain.vo.PostVO;
import com.app.webnest.repository.CommentDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentDAO commentDAO;

    @Override
    public List<CommentDTO> getCommentsByPostId(Long postId) {
        return commentDAO.findCommentPostId(postId);
    }


    @Override
    public Map<String, Long> writeComment(CommentVO commentVO) {
        Map<String, Long> response = new HashMap<>();
        Long newCommentId = commentDAO.saveComment(commentVO);
        response.put("newCommentId", newCommentId);
        return response;
    }
}

