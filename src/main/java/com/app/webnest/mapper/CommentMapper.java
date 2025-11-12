package com.app.webnest.mapper;

import com.app.webnest.domain.dto.CommentDTO;
import com.app.webnest.domain.vo.CommentVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {

    // 댓글 가지고 오기
    List<CommentDTO> selectByPostId(Long id);

    //답글 작성
    public Long insertComment(CommentVO commentVO);
}
