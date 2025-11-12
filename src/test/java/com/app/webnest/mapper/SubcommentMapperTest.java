package com.app.webnest.mapper;

import com.app.webnest.domain.dto.SubcommentDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class SubcommentMapperTest {

    @Autowired
    private SubcommentMapper subcommentMapper;


    @Test
    void selectSubcomment() {
        log.info("selectSubcomment: {}", subcommentMapper.selectSubcomment(1L));

    }
}