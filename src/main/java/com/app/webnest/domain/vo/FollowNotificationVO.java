package com.app.webnest.domain.vo;

import lombok.*;

import java.util.Date;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter @ToString @EqualsAndHashCode(of = "id")
public class FollowNotificationVO {
    private Long id;
    private Integer followNotificationIsRead;
    private Date notificationCreateAt;
    private Long actorUserId;
    private Long receiverUserId;
    private Long followId;
}
