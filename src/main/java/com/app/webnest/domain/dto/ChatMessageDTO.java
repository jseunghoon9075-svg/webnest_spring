package com.app.webnest.domain.dto;

import lombok.*;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter @ToString @EqualsAndHashCode(of = "id")
public class ChatMessageDTO {
    private Long id;
    private Long gameRoomId;
    private String gameRoomTitle;
    private Long userSenderId;
    private String senderNickname;
    private String userSenderTeamcolor;   // 🔁 통일: teamcolor
    private String receiverTeamcolor;     // 🔁 필요하면 활용
    private String senderThumbnailUrl;
    private Integer senderLevel;
    private Long userReceiverId;
    private String receiverNickname;
    private String receiverThumbnailUrl;
    private Integer receiverLevel;
    private String chatMessageContent;
    private String chatMessageType;
    private Boolean chatMessageReadStatus;
    private java.time.LocalDateTime chatMessageCreateAt;
}
