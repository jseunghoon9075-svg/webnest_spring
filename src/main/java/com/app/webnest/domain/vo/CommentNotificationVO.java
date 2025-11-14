package com.app.webnest.domain.vo;

import lombok.*;

import java.util.Date;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter @ToString @EqualsAndHashCode(of = "id")
public class CommentNotificationVO {
    private Long id;
    private Long commentId;
    private Long userId;
    private Date notificationCreateAt;
    private String commentNotificationAction;
    private boolean commentNotificationIsRead;
    private Long actorUserId;
    private Long receiverUserId;
}