package com.app.webnest.service;

import com.app.webnest.domain.dto.CommentNotificationDTO;
import com.app.webnest.domain.dto.FollowNotificationDTO;
import com.app.webnest.domain.dto.PostNotificationDTO;
import com.app.webnest.domain.vo.CommentNotificationVO;
import com.app.webnest.domain.vo.FollowNotificationVO;
import com.app.webnest.domain.vo.PostNotificationVO;

import java.util.List;

public interface NotificationService {
//    알람 조회 전체
    public List<PostNotificationDTO> getPostNotificationByUserId(Long userId);
    public List<CommentNotificationDTO> getCommentNotificationByUserId(Long userId);
    public List<FollowNotificationDTO> getFollowNotificationByUserId(Long userId);

//    알람 추가
    public void addPostNotification(PostNotificationVO postNotificationVO);
    public void addCommentNotification(CommentNotificationVO commentNotificationVO);
    public void addFollowNotification(FollowNotificationVO followNotificationVO);

//    알람 하나를 클릭했을 때
    public void modifyPostNotification(Long id);
    public void modifyFollowNotification(Long id);
    public void modifyCommentNotification(Long id);

    public void modifyEveryPostNotification(Long receiverUserId);
    public void modifyEveryFollowNotification(Long  receiverUserId);
    public void modifyEveryCommentNotification(Long  receiverUserId);

//    알람 삭제 처리
    public void removePostNotification(Long id);
    public void removeFollowNotification(Long id);
    public void removeCommentNotification(Long id);

    public void removeEveryPostNotification(Long receiverUserId);
    public void removeEveryFollowNotification(Long receiverUserId);
    public void removeEveryCommentNotification(Long receiverUserId);

}
