package com.app.webnest.api.privateapi.chat.websocket;

import com.app.webnest.domain.dto.ChatMessageDTO;
import com.app.webnest.domain.vo.ChatMessageVO;
import com.app.webnest.domain.vo.GameJoinVO;
import com.app.webnest.service.ChatMessageService;
import com.app.webnest.service.GameJoinService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatMessageApi {

    private final ChatMessageService chatMessageService;
    private final GameJoinService gameJoinService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/chats/send")
    public void sendMessage(ChatMessageVO chatMessageVO) {
        log.info("Sending message to chat: {}", chatMessageVO);
        ChatMessageDTO chatMessageDTO = null;
        String type = chatMessageVO.getChatMessageType();

        // 유효하지 않은 userSenderId 체크
        if(chatMessageVO.getUserSenderId() == null || chatMessageVO.getUserSenderId() <= 0) {
            log.warn("Invalid userSenderId: {}", chatMessageVO.getUserSenderId());
            return;
        }

        GameJoinVO gameJoinVO = new GameJoinVO(chatMessageVO);
        Optional<GameJoinVO> existingGameJoin = gameJoinService.getGameJoinDTOByGameRoomId(gameJoinVO);
        boolean alreadyExistUserInRoom = existingGameJoin.isPresent();
        
        log.info("Checking user in room. userId: {}, gameRoomId: {}, exists: {}, teamColor from VO: {}", 
                gameJoinVO.getUserId(), gameJoinVO.getGameRoomId(), alreadyExistUserInRoom, 
                chatMessageVO.getUserSenderTeamcolor());

        if(type.equals("JOIN")){
            if(!alreadyExistUserInRoom){
                // gameJoinIsHost가 null이면 기본값 0 설정
                if(gameJoinVO.getGameJoinIsHost() == null) {
                    gameJoinVO.setGameJoinIsHost(0);
                }
                // 팀 컬러가 null이면 경고
                if(gameJoinVO.getGameJoinTeamcolor() == null || gameJoinVO.getGameJoinTeamcolor().isEmpty()) {
                    log.warn("⚠️ Team color is null when joining room. userId: {}, gameRoomId: {}. 프론트에서 userSenderTeamcolor를 보내야 합니다!", 
                            gameJoinVO.getUserId(), gameJoinVO.getGameRoomId());
                } else {
                    log.info("✅ Team color provided: userId: {}, gameRoomId: {}, teamColor: {}", 
                            gameJoinVO.getUserId(), gameJoinVO.getGameRoomId(), gameJoinVO.getGameJoinTeamcolor());
                }
                gameJoinService.join(gameJoinVO);
                log.info("User joined room. userId: {}, gameRoomId: {}, teamColor: {}", 
                        gameJoinVO.getUserId(), gameJoinVO.getGameRoomId(), gameJoinVO.getGameJoinTeamcolor());
            } else {
                // 이미 존재하는 경우
                GameJoinVO existing = existingGameJoin.get();
                log.info("User already exists in room. userId: {}, gameRoomId: {}, teamColor in DB: {}", 
                        existing.getUserId(), existing.getGameRoomId(), existing.getGameJoinTeamcolor());
                
                // DB에 팀 컬러가 없고 프론트에서 보낸 팀 컬러가 있으면 업데이트
                if((existing.getGameJoinTeamcolor() == null || existing.getGameJoinTeamcolor().isEmpty()) 
                   && chatMessageVO.getUserSenderTeamcolor() != null 
                   && !chatMessageVO.getUserSenderTeamcolor().isEmpty()) {
                    existing.setGameJoinTeamcolor(chatMessageVO.getUserSenderTeamcolor());
                    gameJoinService.updateTeamColor(existing);
                    log.info("✅ Updated team color for existing user. userId: {}, gameRoomId: {}, teamColor: {}", 
                            existing.getUserId(), existing.getGameRoomId(), existing.getGameJoinTeamcolor());
                } else if(existing.getGameJoinTeamcolor() == null || existing.getGameJoinTeamcolor().isEmpty()) {
                    log.warn("⚠️ User exists but team color is null in DB. userId: {}, gameRoomId: {}. 프론트에서 userSenderTeamcolor를 보내야 합니다!", 
                            existing.getUserId(), existing.getGameRoomId());
                }
            }
        }else if(type.equals("LEAVE")){
            gameJoinService.leave(gameJoinVO);
        }else if(type.equals("MESSAGE")){
            // MESSAGE 전에 TBL_GAME_JOIN에 팀 컬러가 있는지 확인
            if(alreadyExistUserInRoom) {
                GameJoinVO existing = existingGameJoin.get();
                log.info("🔍 User exists in room. userId: {}, gameRoomId: {}, teamColor in DB: {}", 
                        existing.getUserId(), existing.getGameRoomId(), existing.getGameJoinTeamcolor());
                // 팀 컬러가 없고 프론트에서 보낸 팀 컬러가 있으면 업데이트
                if((existing.getGameJoinTeamcolor() == null || existing.getGameJoinTeamcolor().isEmpty()) 
                   && chatMessageVO.getUserSenderTeamcolor() != null 
                   && !chatMessageVO.getUserSenderTeamcolor().isEmpty()) {
                    existing.setGameJoinTeamcolor(chatMessageVO.getUserSenderTeamcolor());
                    gameJoinService.updateTeamColor(existing);
                    log.info("Updated team color for MESSAGE. userId: {}, gameRoomId: {}, teamColor: {}", 
                            existing.getUserId(), existing.getGameRoomId(), existing.getGameJoinTeamcolor());
                }
            } else {
                log.warn("User not found in TBL_GAME_JOIN when sending MESSAGE. userId: {}, gameRoomId: {}", 
                        chatMessageVO.getUserSenderId(), chatMessageVO.getGameRoomId());
            }
            
            chatMessageService.sendChat(chatMessageVO);
            // sendChat 후 생성된 id를 사용하여 조회
            // 프론트에서 id를 보내지 않아도 됨 (백엔드에서 자동 생성)
            // id가 0이 아닌 유효한 값일 때만 조회
            if(chatMessageVO.getId() != null && chatMessageVO.getId() > 0) {
                chatMessageDTO = chatMessageService.getChatByRoomId(chatMessageVO);
                if(chatMessageDTO != null) {
                    log.info("Retrieved chat message. id: {}, userSenderTeamcolor: {}", 
                            chatMessageDTO.getId(), chatMessageDTO.getUserSenderTeamcolor());
                }
            } else {
                log.warn("Failed to get generated id after sendChat. chatMessageVO: {}", chatMessageVO);
            }
        } else {
            // JOIN이나 LEAVE 타입일 때는 조회하지 않음
            return;
        }

        // 브로드 캐스트
        if(chatMessageDTO != null){
            if(chatMessageVO.getUserReceiverId() == null){
                // receiver가 없을 때, 방 전체 전송
                simpMessagingTemplate.convertAndSend(
                        "/sub/chats/room/" + chatMessageVO.getGameRoomId(),
                        chatMessageDTO
                );

            }else{
                // 1:1 메세지
                simpMessagingTemplate.convertAndSend(
                        "/sub/chats/room/" + chatMessageVO.getGameRoomId() + "/" + chatMessageVO.getUserReceiverId(),
                        chatMessageDTO
                );
            }
        }
    }


}
