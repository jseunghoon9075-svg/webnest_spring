package com.app.webnest.domain.dto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FollowNotificationDTO {
    private Long id;
    private Date notificationCreateAt;
    private Long actorUserId;
    private String userNickname;
    private int userLevel;
    private String userThumbnailUrl;
}
