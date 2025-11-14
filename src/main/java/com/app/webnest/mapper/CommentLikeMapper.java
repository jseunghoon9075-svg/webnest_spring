package com.app.webnest.mapper;

import com.app.webnest.domain.vo.CommentLikeVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface CommentLikeMapper {
    // 게시글 상세에서 좋아요 개수
    int selectByPostIdcount (Long commentId);





    public int isLiked(Map<String, Long> map);

    public void insertLike(Map<String, Long> map);

    public void deleteLike(Map<String, Long> map);

    public int countLike(Long commentId);
}
