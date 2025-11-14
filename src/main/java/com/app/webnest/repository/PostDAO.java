package com.app.webnest.repository;

import com.app.webnest.domain.dto.PostResponseDTO;
import com.app.webnest.domain.vo.PostVO;
import com.app.webnest.mapper.PostMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostDAO {

    private final PostMapper postMapper;
    // 게시글 단일 조회
    public Optional<PostResponseDTO> findPost(Long id) {
        return postMapper.select(id);
    }

    // 열린둥지 툴바 사용자 지정…
    public List<PostResponseDTO> findOpenPosts() {
        return postMapper.selectAllOpen();
    }

    // 문제둥지 툴바 사용자 지정…
    public List<PostResponseDTO> findQuestionPosts() {
        return postMapper.selectAllQuestion();
    }



    // 마이페이지 - 열린둥지
    public List<PostResponseDTO> findOpenPostsByUserId(Long userId){
        return postMapper.selectAllOpenByUserId(userId);
    }

    // 마이페이지 - 문제둥지
    public List<PostResponseDTO> findQuestionPostsByUserId(Long userId){
        return postMapper.selectAllQuestionByUserId(userId);
    }


    //게시글 작성
    public Long savePost(PostVO postVO){
        postMapper.insertPost(postVO);
        return postVO.getId();
    }
    // 게시글 조회 수 증가
    public void updateReadCount(Long id){
        postMapper.updatePostViewCount(id);
    }




    /// ggggggggggggggggggggggg

    public boolean isPostLiked(Long postId, Long userId) {
        Map<String, Long> map = new HashMap<>();
        map.put("postId", postId);
        map.put("userId", userId);
        return postMapper.isPostLiked(map) > 0;
    }

    public void addPostLike(Long postId, Long userId) {
        Map<String, Long> map = new HashMap<>();
        map.put("postId", postId);
        map.put("userId", userId);
        postMapper.insertLike(map);
    }

    public void removePostLike(Long postId, Long userId) {
        Map<String, Long> map = new HashMap<>();
        map.put("postId", postId);
        map.put("userId", userId);
        postMapper.deleteLike(map);
    }

    public int getPostLikeCount(Long postId) {
        return postMapper.selectPostLikeCount(postId);
    }





}
