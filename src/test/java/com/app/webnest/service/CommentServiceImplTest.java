package com.app.webnest.service;

import com.app.webnest.domain.dto.CommentDTO;
import com.app.webnest.mapper.CommentMapper;
import com.app.webnest.repository.CommentDAO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class CommentServiceImplTest {
    @Autowired
    private CommentDAO commentDAO;
    @Test
    void getCommentsByPostIffd() {
        commentDAO.findCommentPostId(1L).stream().map(CommentDTO::toString).forEach(log::info);
    }
}