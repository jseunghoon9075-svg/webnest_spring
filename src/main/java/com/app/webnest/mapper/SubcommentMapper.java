package com.app.webnest.mapper;

import com.app.webnest.domain.dto.SubcommentDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SubcommentMapper {
    List<SubcommentDTO> selectSubcomment(Long commentId);
}
//// 게시글 상세조회
//public Optional<PostDTO> selectOne(Long id);
//
//// 게시글의 댓글 조회
//public List<CommentDTO> selectComment(Long postTestId);
//
//// 게시글의 대댓글 조회
//public List<ReplyDTO> selectReply(Long commentTestId);
