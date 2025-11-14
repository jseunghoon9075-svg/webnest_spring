package com.app.webnest.service;

import com.app.webnest.domain.dto.CommentNotificationDTO;
import com.app.webnest.domain.dto.FollowNotificationDTO;
import com.app.webnest.domain.dto.PostNotificationDTO;
import com.app.webnest.domain.vo.CommentNotificationVO;
import com.app.webnest.domain.vo.FollowNotificationVO;
import com.app.webnest.domain.vo.PostNotificationVO;
import com.app.webnest.repository.NotificationDAO;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class NotificationServiceImpl implements NotificationService {

    private final NotificationDAO notificationDAO;

    @Override
    public List<PostNotificationDTO> getPostNotificationByUserId(Long userId) {
        if(!notificationDAO.findPostNotificationByUserId(userId).isEmpty()){
            return notificationDAO.findPostNotificationByUserId(userId);
        }
        return new ArrayList<PostNotificationDTO>();
    }

    @Override
    public List<CommentNotificationDTO> getCommentNotificationByUserId(Long userId) {
        if(!notificationDAO.findCommentNotificationByUserId(userId).isEmpty()){
            return notificationDAO.findCommentNotificationByUserId(userId);
        }
        return new ArrayList<CommentNotificationDTO>();
    }

    @Override
    public List<FollowNotificationDTO> getFollowNotificationByUserId(Long userId) {
        if(!notificationDAO.findFollowNotificationByUserId(userId).isEmpty()){
            return notificationDAO.findFollowNotificationByUserId(userId);
        }
        return new ArrayList<FollowNotificationDTO>();
    }

    @Override
    public void addPostNotification(PostNotificationVO postNotificationVO) {
        notificationDAO.savePostNotification(postNotificationVO);
    }

    @Override
    public void addCommentNotification(CommentNotificationVO commentNotificationVO) {
        notificationDAO.saveCommentNotification(commentNotificationVO);
    }

    @Override
    public void addFollowNotification(FollowNotificationVO followNotificationVO) {
        notificationDAO.saveFollowNotification(followNotificationVO);
    }

    @Override
    public void modifyPostNotification(Long id) {
        notificationDAO.modifyPostNotification(id);
    }

    @Override
    public void modifyFollowNotification(Long id) {
        notificationDAO.modifyFollowNotification(id);
    }

    @Override
    public void modifyCommentNotification(Long id) {
        notificationDAO.modifyCommentNotification(id);
    }

    @Override
    public void modifyEveryPostNotification(Long receiverUserId) {
        notificationDAO.modifyEveryPostsNotification(receiverUserId);
    }

    @Override
    public void modifyEveryFollowNotification(Long receiverUserId) {
        notificationDAO.modifyEveryFollowNotification(receiverUserId);
    }

    @Override
    public void modifyEveryCommentNotification(Long receiverUserId) {
        notificationDAO.modifyEveryCommentsNotification(receiverUserId);
    }

    @Override
    public void removePostNotification(Long id) {
        notificationDAO.removePostNotification(id);
    }

    @Override
    public void removeFollowNotification(Long id) {
        notificationDAO.removeFollowNotification(id);
    }

    @Override
    public void removeCommentNotification(Long id) {
        notificationDAO.removeCommentNotification(id);
    }

    @Override
    public void removeEveryPostNotification(Long receiverUserId) {
        notificationDAO.removeEveryPostsNotification(receiverUserId);
    }

    @Override
    public void removeEveryFollowNotification(Long receiverUserId) {
        notificationDAO.removeEveryFollowNotification(receiverUserId);
    }

    @Override
    public void removeEveryCommentNotification(Long receiverUserId) {
        notificationDAO.removeEveryCommentsNotification(receiverUserId);
    }
}
