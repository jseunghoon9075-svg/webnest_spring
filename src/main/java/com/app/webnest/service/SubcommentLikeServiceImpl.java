package com.app.webnest.service;

import com.app.webnest.domain.vo.SubcommentLikeVO;
import com.app.webnest.repository.CommentLikeDAO;
import com.app.webnest.repository.SubcommentLikeDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class SubcommentLikeServiceImpl implements SubcommentLikeService {
    private final SubcommentLikeDAO subcommentLikeDAO;

    @Override
    public void save(SubcommentLikeVO  subcommentLikeVO) {
        subcommentLikeDAO.save(subcommentLikeVO);
    }

    @Override
    public int getSubcommentLike(Long subcommentId) {
        return subcommentLikeDAO.findSubcommentLike(subcommentId);
    }
}

