package com.app.webnest.mapper;

import com.app.webnest.domain.dto.CommentNotificationDTO;
import com.app.webnest.domain.dto.FollowNotificationDTO;
import com.app.webnest.domain.dto.PostNotificationDTO;
import com.app.webnest.domain.dto.PostSearchDTO;
import com.app.webnest.domain.vo.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface NotificationMapper {

    //                  알람
//    post에 대한 알림
    public List<PostNotificationDTO> selectPostNotificationByUserId(Long receiverUserId);
    //    comment 알림 test 1L / 3L
    public List<CommentNotificationDTO> selectCommentNotificationByUserId(Long receiverUserId);
    //    newFollow 알림 test 1L
    public List<FollowNotificationDTO> selectFollowNotificationByUserId(Long receiverUserId);

//    알람 추가
    public void insetPostNotification(PostNotificationVO  postNotificationVO);
    public void insertCommentNotification(CommentNotificationVO commentNotificationVO);
    public void insertFollowNotification(FollowNotificationVO followNotificationVO);

//    알람 수정(읽기)
    public void updatePostsNotification(Long id);
    public void updateCommentsNotification(Long id);
    public void updateFollowNotification(Long id);

    public void updateAllPostsNotification(Long receiverUserId);
    public void updateAllCommentsNotification(Long receiverUserId);
    public void updateAllFollowNotification(Long receiverUserId);

//    알람 삭제(삭제하기)
    public void deletePostNotification(Long id);
    public void deleteCommentNotification(Long id);
    public void deleteFollowNotification(Long id);

    public void deleteAllPostNotificationByUserId(Long receiverUserId);
    public void deleteAllCommentNotificationByUserId(Long receiverUserId);
    public void deleteAllFollowNotificationByUserId(Long receiverUserId);
}
