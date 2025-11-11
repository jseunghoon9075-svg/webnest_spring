package com.app.webnest.service;

import com.app.webnest.domain.dto.CommentDTO;
import com.app.webnest.repository.CommentDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentDAO commentDAO;

    @Override
    public List<CommentDTO> getCommentsByPostId(Long postId) {
        return commentDAO.findCommentPostId(postId);
    }
}
