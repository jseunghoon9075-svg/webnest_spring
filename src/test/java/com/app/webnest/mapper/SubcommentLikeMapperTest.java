package com.app.webnest.mapper;

import com.app.webnest.domain.vo.SubcommentLikeVO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class SubcommentLikeMapperTest {
    @Autowired
    SubcommentLikeMapper subcommentLikeMapper;

    @Test
    void insert() {
        SubcommentLikeVO subcommentLikeVO = new SubcommentLikeVO();
        subcommentLikeVO.setUserId(20L);
        subcommentLikeVO.setSubcommentId(3L);
        subcommentLikeMapper.insert(subcommentLikeVO);
    }

    @Test
    void selectByPostIdcount() {
    }

    @Test
    void delete() {
      subcommentLikeMapper.delete(3L);


    }
}