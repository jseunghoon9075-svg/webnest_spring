package com.app.webnest.service;

import com.app.webnest.domain.vo.SubcommentLikeVO;

public interface SubcommentLikeService {
    public void save(SubcommentLikeVO subcommentLikeVO);
    public int getSubcommentLike(Long subcommentId);
}
