package com.app.webnest.domain.vo;

import lombok.*;

import java.util.Date;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter @ToString @EqualsAndHashCode(of = "id")
public class PostNotificationVO {
    private Long id;
    private Long userId;
    private Long postId;
    private Date notificationCreateAt;
    private Integer postNotificationIsRead;
    private String postNotificationContent; // erd수정
    private String postNotificationAction;
    private Long actorUserId;
    private Long receiverUserId;
}
