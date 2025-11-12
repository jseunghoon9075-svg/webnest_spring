package com.app.webnest.repository;

import com.app.webnest.mapper.CommentMapper;
import com.app.webnest.mapper.SubcommentMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class SubcommentDAOTest {
    @Autowired
    private SubcommentDAO subcommentDAO;

    @Test
    void findAll() {
        log.info("selectSubcomment: {}", subcommentDAO.findAll(1L));
    }
}