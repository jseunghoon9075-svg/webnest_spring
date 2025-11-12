package com.app.webnest.domain.dto;

import lombok.*;

import java.util.Date;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CommentNotificationDTO {
    private Long id;
    private String commentNotificationAction;
    private Integer commentNotificationIsRead;
    private Date notificationCreateAt;
    private String userThumbnailUrl;
    private int userLevel;
    private String userNickname;
    private Long actorUserId;
    private Long receiverUserId;
    private Long commentId;
    private Long postId;
    private String postTitle;
    private String postContent;
}
