package com.app.webnest.service;

import com.app.webnest.domain.dto.SubcommentDTO;
import com.app.webnest.repository.SubcommentDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SubcommentServiceImpl implements SubcommentService {
    private final SubcommentDAO subcommentDAO;

    @Override
    public List<SubcommentDTO> getSubcomments(Long commentId) {
        return  subcommentDAO.findAll(commentId);
    }
}
//public List<SubcommentDTO> findAll(Long commentId) {return subcommentMapper.selectSubcomment(commentId);}