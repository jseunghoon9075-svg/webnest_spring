package com.app.webnest.api.privateapi.chat.websocket;

import com.app.webnest.domain.dto.ChatMessageDTO;
import com.app.webnest.domain.dto.GameJoinDTO;
import com.app.webnest.domain.vo.ChatMessageVO;
import com.app.webnest.domain.vo.GameJoinVO;
import com.app.webnest.exception.GameJoinException;
import com.app.webnest.service.ChatMessageService;
import com.app.webnest.service.GameJoinService;
import com.app.webnest.service.GameRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatMessageApi {

    private final ChatMessageService chatMessageService;
    private final GameJoinService gameJoinService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final GameRoomService gameRoomService;

    @MessageMapping("/chats/send")
    public void sendMessage(ChatMessageVO chatMessageVO) {
        log.info("📨 채팅 메시지 수신 - type: {}, userSenderId: {}, gameRoomId: {}, content: {}", 
                chatMessageVO.getChatMessageType(), 
                chatMessageVO.getUserSenderId(), 
                chatMessageVO.getGameRoomId(),
                chatMessageVO.getChatMessageContent());
        ChatMessageDTO chatMessageDTO = null;
        String type = chatMessageVO.getChatMessageType();

        // 유효하지 않은 userSenderId 체크
        if(chatMessageVO.getUserSenderId() == null || chatMessageVO.getUserSenderId() <= 0) {
            log.warn("❌ Invalid userSenderId: {}", chatMessageVO.getUserSenderId());
            return;
        }

        // 게임방 ID 유효성 검증
        Long gameRoomId = chatMessageVO.getGameRoomId();
        if (gameRoomId == null || gameRoomId <= 0) {
            log.error("❌ Invalid gameRoomId: {}. 전체 요청을 거부합니다.", gameRoomId);
            return;
        }
        log.info("🔍 게임방 ID 검증 완료 - gameRoomId: {}", gameRoomId);

        GameJoinVO gameJoinVO = new GameJoinVO(chatMessageVO);
        Optional<GameJoinVO> existingGameJoin = gameJoinService.getGameJoinDTOByGameRoomId(gameJoinVO);
        boolean alreadyExistUserInRoom = existingGameJoin.isPresent();
        
        log.info("👤 사용자 방 참여 상태 확인 - userId: {}, gameRoomId: {}, alreadyInRoom: {}, teamColor: {}", 
                gameJoinVO.getUserId(), gameJoinVO.getGameRoomId(), alreadyExistUserInRoom, 
                chatMessageVO.getUserSenderTeamcolor());

        if(type.equals("JOIN")){
            // 게임방 존재 여부 확인 (이미 위에서 gameRoomId 검증 완료)
            try {
                gameRoomService.getRoom(gameRoomId);
                log.info("✅ 게임방 존재 확인 완료 (JOIN) - gameRoomId: {}", gameRoomId);
            } catch (Exception e) {
                log.error("❌ 게임방이 존재하지 않습니다 - gameRoomId: {}, error: {}", gameRoomId, e.getMessage());
                // 에러 메시지를 클라이언트에 전송
                simpMessagingTemplate.convertAndSend(
                    "/sub/chats/room/" + gameRoomId,
                    Map.of("type", "ERROR", "message", "게임방이 존재하지 않습니다. ID: " + gameRoomId)
                );
                return;
            }
            
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
                
                // 호스트가 없는지 확인하고 자동으로 호스트 지정
                List<GameJoinDTO> allPlayers = gameJoinService.getPlayers(gameRoomId);
                boolean hasHost = allPlayers.stream()
                        .anyMatch(player -> player.isGameJoinIsHost());
                
                if (!hasHost && !allPlayers.isEmpty()) {
                    // 호스트가 없으면 첫 번째 플레이어를 호스트로 지정
                    GameJoinDTO firstPlayer = allPlayers.get(0);
                    log.warn("⚠️ 호스트가 없습니다 - 첫 번째 플레이어를 호스트로 지정. userId: {}, gameRoomId: {}", 
                            firstPlayer.getUserId(), gameRoomId);
                    
                    GameJoinVO hostVO = new GameJoinVO();
                    hostVO.setUserId(firstPlayer.getUserId());
                    hostVO.setGameRoomId(gameRoomId);
                    hostVO.setGameJoinIsHost(1);
                    gameJoinService.update(hostVO);
                    log.info("✅ 호스트 자동 지정 완료 - userId: {}, gameRoomId: {}", 
                            firstPlayer.getUserId(), gameRoomId);
                }
            } else {
                // 이미 존재하는 경우 - 호스트 정보는 절대 변경하지 않음
                GameJoinVO existing = existingGameJoin.get();
                log.info("User already exists in room. userId: {}, gameRoomId: {}, isHost: {}, teamColor in DB: {}", 
                        existing.getUserId(), existing.getGameRoomId(), existing.getGameJoinIsHost(), existing.getGameJoinTeamcolor());
                
                // 호스트 정보 보호: DB의 호스트 정보를 유지 (프론트에서 보낸 값으로 덮어쓰지 않음)
                // 팀 컬러만 업데이트 (호스트 정보는 변경하지 않음)
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
//            게임방 퇴장 로직
        }else if(type.equals("LEAVE")){
            Long currentRoomId = gameJoinVO.getGameRoomId();
            log.info("🚪 사용자 퇴장 요청 - userId: {}, gameRoomId: {}", gameJoinVO.getUserId(), currentRoomId);
            
            // 현재 사용자 정보 조회
            GameJoinVO currentUser = gameJoinService.getGameJoinDTOByGameRoomId(gameJoinVO)
                    .orElseThrow(() -> {
                        log.error("❌ 퇴장하려는 유저를 찾을 수 없습니다 - userId: {}, gameRoomId: {}", 
                                gameJoinVO.getUserId(), currentRoomId);
                        throw new GameJoinException("유저를 찾을 수 없습니다.");
                    });
            
            // 현재 방의 모든 플레이어 조회
            List<GameJoinVO> foundPlayers = gameJoinService.getUserListByEntrancedTime(currentRoomId);
            log.info("👥 현재 방의 플레이어 수: {}, 나가는 유저 isHost: {}", 
                    foundPlayers.size(), currentUser.getGameJoinIsHost());
            
            // 혼자 남은 유저가 나간 경우 -> 방 폭파
            if(foundPlayers.size() <= 1){
                log.info("🗑️ 마지막 유저 퇴장 - 방 삭제 예정. gameRoomId: {}", currentRoomId);
                gameJoinService.leave(gameJoinVO);
                gameRoomService.delete(currentRoomId);
                log.info("✅ 방 삭제 완료 - gameRoomId: {}", currentRoomId);
            } else {
                // 호스트가 나가는 경우에만 호스트 전환
                boolean isHostLeaving = currentUser.getGameJoinIsHost() != null && currentUser.getGameJoinIsHost() == 1;
                
                if(isHostLeaving) {
                    log.info("👑 호스트가 퇴장합니다 - 호스트 전환 필요. currentHostId: {}", currentUser.getUserId());
                    
                    // 현재 사용자의 인덱스 찾기 (userId로 비교)
                    int currentUserIndex = -1;
                    for (int i = 0; i < foundPlayers.size(); i++) {
                        if (foundPlayers.get(i).getUserId().equals(currentUser.getUserId())) {
                            currentUserIndex = i;
                            break;
                        }
                    }
                    
                    if (currentUserIndex == -1) {
                        log.error("❌ 현재 사용자를 플레이어 목록에서 찾을 수 없습니다 - userId: {}", currentUser.getUserId());
                        // 인덱스를 찾지 못해도 퇴장은 처리
                        gameJoinService.leave(gameJoinVO);
                        return;
                    }
                    
                    // 다음 유저 찾기 (현재 유저 제외)
                    GameJoinVO nextUser = null;
                    for (int i = 0; i < foundPlayers.size(); i++) {
                        if (i != currentUserIndex) {
                            nextUser = foundPlayers.get(i);
                            break;
                        }
                    }
                    
                    if (nextUser != null) {
                        log.info("✅ 호스트 전환 - 새 호스트: userId: {}", nextUser.getUserId());
                        nextUser.setGameJoinIsHost(1);
                        gameJoinService.update(nextUser);
                    } else {
                        log.error("❌ 호스트 전환할 다음 유저를 찾을 수 없습니다");
                    }
                }
                
                // 사용자 퇴장 처리
                gameJoinService.leave(gameJoinVO);
                log.info("✅ 사용자 퇴장 완료 - userId: {}, gameRoomId: {}", gameJoinVO.getUserId(), currentRoomId);
                
                // 퇴장 후 남은 플레이어 수 확인
                List<GameJoinVO> remainingPlayers = gameJoinService.getUserListByEntrancedTime(currentRoomId);
                if (remainingPlayers.isEmpty()) {
                    log.warn("⚠️ 퇴장 후 방에 아무도 없습니다 - 방 삭제 예정. gameRoomId: {}", currentRoomId);
                    gameRoomService.delete(currentRoomId);
                    log.info("✅ 빈 방 삭제 완료 - gameRoomId: {}", currentRoomId);
                } else {
                    log.info("👥 퇴장 후 남은 플레이어 수: {}", remainingPlayers.size());
                }
            }
        }else if(type.equals("MESSAGE")){
            // 게임방 존재 여부 확인 (이미 위에서 gameRoomId 검증 완료)
            try {
                gameRoomService.getRoom(gameRoomId);
                log.info("✅ 게임방 존재 확인 완료 (MESSAGE) - gameRoomId: {}", gameRoomId);
            } catch (Exception e) {
                log.error("❌ 게임방이 존재하지 않습니다 (MESSAGE) - gameRoomId: {}, error: {}", gameRoomId, e.getMessage());
                // 에러 메시지를 클라이언트에 전송
                simpMessagingTemplate.convertAndSend(
                    "/sub/chats/room/" + gameRoomId,
                    Map.of("type", "ERROR", "message", "게임방이 존재하지 않습니다. ID: " + gameRoomId)
                );
                return;
            }
            
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
