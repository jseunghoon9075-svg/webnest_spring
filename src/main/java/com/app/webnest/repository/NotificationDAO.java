package com.app.webnest.repository;

import com.app.webnest.domain.dto.CommentNotificationDTO;
import com.app.webnest.domain.dto.FollowNotificationDTO;
import com.app.webnest.domain.dto.PostNotificationDTO;
import com.app.webnest.domain.vo.CommentNotificationVO;
import com.app.webnest.domain.vo.FollowNotificationVO;
import com.app.webnest.domain.vo.PostNotificationVO;
import com.app.webnest.mapper.NotificationMapper;
import com.app.webnest.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class NotificationDAO {
    private final NotificationMapper notificationMapper;

// 알람 추가 ( 포스트 , 댓글, 팔로우 )
    public void savePostNotification(PostNotificationVO postNotificationVO) {
        notificationMapper.insetPostNotification(postNotificationVO);
    }
    public void saveCommentNotification(CommentNotificationVO commentNotificationVO) {
        notificationMapper.insertCommentNotification(commentNotificationVO);
    }
    public void saveFollowNotification(FollowNotificationVO followNotificationVO) {
        notificationMapper.insertFollowNotification(followNotificationVO);
    }

// 알람 가져오기 ( 포스트 , 댓글, 팔로우 )
    public List<PostNotificationDTO> findPostNotificationByUserId(Long userId) {
        return notificationMapper.selectPostNotificationByUserId(userId);
    }
    public List<CommentNotificationDTO> findCommentNotificationByUserId(Long userId) {
        return notificationMapper.selectCommentNotificationByUserId(userId);
    }
    public List<FollowNotificationDTO> findFollowNotificationByUserId(Long userId) {
        return notificationMapper.selectFollowNotificationByUserId(userId);
    }
//    알람 수정하기 (읽기 처리 / 전체 읽기)
//           단건 읽기
    public void modifyPostNotification(Long id) {
        notificationMapper.updatePostsNotification(id);
    }
    public void modifyCommentNotification(Long id) {
        notificationMapper.updateCommentsNotification(id);
    }
    public void modifyFollowNotification(Long id) {
        notificationMapper.updateFollowNotification(id);
    }

//          전체 읽기 처리 ( 유저 아이디 )
    public void modifyEveryPostsNotification(Long receiverUserId) {
        notificationMapper.updateAllPostsNotification(receiverUserId);
    }
    public void modifyEveryCommentsNotification(Long receiverUserId) {
        notificationMapper.updateAllCommentsNotification(receiverUserId);
    }
    public void modifyEveryFollowNotification(Long receiverUserId) {
        notificationMapper.updateAllFollowNotification(receiverUserId);
    }
//    알람 삭제하기 ( 한 건 / 다 건 )
    public void removePostNotification(Long id) {
        notificationMapper.deletePostNotification(id);
    }
    public void removeCommentNotification(Long id) {
        notificationMapper.deleteCommentNotification(id);
    }
    public void removeFollowNotification(Long id) {
        notificationMapper.deleteFollowNotification(id);
    }

//            유저 알람 전체
    public void removeEveryPostsNotification(Long receiverUserId) {
        notificationMapper.deleteAllPostNotificationByUserId(receiverUserId);
    }
    public void removeEveryCommentsNotification(Long receiverUserId) {
        notificationMapper.deleteAllCommentNotificationByUserId(receiverUserId);
    }
    public void removeEveryFollowNotification(Long receiverUserId) {
        notificationMapper.deleteAllFollowNotificationByUserId(receiverUserId);
    }

}
