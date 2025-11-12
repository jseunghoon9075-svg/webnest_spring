package com.app.webnest.repository;

import com.app.webnest.domain.dto.SubcommentDTO;
import com.app.webnest.mapper.SubcommentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SubcommentDAO {
    private final SubcommentMapper subcommentMapper;

    public List<SubcommentDTO> findAll(Long commentId) {return subcommentMapper.selectSubcomment(commentId);}

}
