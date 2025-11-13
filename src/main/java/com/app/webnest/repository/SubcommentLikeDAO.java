package com.app.webnest.repository;

import com.app.webnest.domain.vo.SubcommentLikeVO;
import com.app.webnest.mapper.CommentLikeMapper;
import com.app.webnest.mapper.SubcommentLikeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SubcommentLikeDAO {
    private final SubcommentLikeMapper subcommentLikeMapper;

    public void save(SubcommentLikeVO subcommentLikeVO) {
        subcommentLikeMapper.insert(subcommentLikeVO);
    }
    public int findSubcommentLike(Long subcommentId) {
        return subcommentLikeMapper.selectByPostIdcount(subcommentId);
    }
}
