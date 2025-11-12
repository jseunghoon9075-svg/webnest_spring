package com.app.webnest.service;



import com.app.webnest.domain.dto.SubcommentDTO;

import java.util.List;

public interface SubcommentService {
    // 게시글 목록
    public List<SubcommentDTO> getSubcomments(Long commentId);
}
//public List<SubcommentDTO> findAll(Long commentId) {return subcommentMapper.selectSubcomment(commentId);}