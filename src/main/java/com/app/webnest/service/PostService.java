package com.app.webnest.service;

import com.app.webnest.domain.dto.PostResponseDTO;
import com.app.webnest.domain.vo.PostVO;

import java.util.List;
import java.util.Map;


public interface PostService {
    // 상세 페이지 조회
    public PostResponseDTO getPost(Long id, Long userId);

    //조회수 증가 안함 상세조회
    PostResponseDTO getPostWithoutView(Long id, Long userId);



    // 열린둥지
    List<PostResponseDTO> getOpenPosts();

    // 문제둥지
    List<PostResponseDTO> getQuestionPosts();

    //  마이페이지 - 열린둥지
    List<PostResponseDTO> getOpenPostsByUserId(Long userId);

    // 마이페이지 - 문제둥지
    List<PostResponseDTO> getQuestionPostsByUserId(Long userId);

   // 게시글 추가
    public Map<String, Long> write(PostVO postVO);


    public Map<String, Object> togglePostLike(Long postId, Long userId);






}



//
//
//    // 게시글 수정
//    public void modify(PostVO postVO);
//
//    // 게시글 삭제
//    public void remove(Long id);
//}
//
