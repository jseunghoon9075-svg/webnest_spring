package com.app.webnest.repository;

import com.app.webnest.domain.dto.CommentDTO;
import com.app.webnest.domain.dto.PostResponseDTO;
import com.app.webnest.mapper.CommentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class CommentDAO {
    private final CommentMapper commentMapper;

    public List<CommentDTO> findCommentPostId(Long postId) {
        return commentMapper.selectByPostId(postId);
    }
}
