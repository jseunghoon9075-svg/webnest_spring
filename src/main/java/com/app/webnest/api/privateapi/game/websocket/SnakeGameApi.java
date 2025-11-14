package com.app.webnest.api.privateapi.game.websocket;

import com.app.webnest.domain.dto.GameJoinDTO;
import com.app.webnest.domain.dto.GameRoomDTO;
import com.app.webnest.domain.vo.GameJoinVO;
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
import java.util.Random;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SnakeGameApi {

    private final GameJoinService gameJoinService;
    private final GameRoomService gameRoomService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    /**
     * 준비 상태 업데이트
     * /pub/game/snake/ready
     * 요청: { gameRoomId: Long, userId: Long, gameJoinIsReady: 1 }
     */
    @MessageMapping("/game/snake/ready")
    public void updateReady(GameJoinVO gameJoinVO) {
        log.info("Ready status update requested. gameRoomId: {}, userId: {}, isReady: {}", 
                gameJoinVO.getGameRoomId(), gameJoinVO.getUserId(), gameJoinVO.getGameJoinIsReady());
        
        // 준비 상태 업데이트
        gameJoinService.updateReady(gameJoinVO);
        
        // 게임 상태 조회
        List<GameJoinDTO> gameState = gameJoinService.getArrangeUserByTurn(gameJoinVO.getGameRoomId());
        
        Map<String, Object> response = new HashMap<>();
        response.put("type", "READY_UPDATED");
        response.put("gameState", gameState);
        
        // 브로드캐스트
        simpMessagingTemplate.convertAndSend(
                "/sub/game/snake/room/" + gameJoinVO.getGameRoomId(),
                response
        );
    }

    /**
     * 게임 시작
     * /pub/game/snake/start
     * 요청: { gameRoomId: Long, userId: Long } (방장의 userId)
     */
    @MessageMapping("/game/snake/start")
    public void startGame(GameJoinVO gameJoinVO) {
        log.info("Game start requested. gameRoomId: {}, userId: {}", 
                gameJoinVO.getGameRoomId(), gameJoinVO.getUserId());
        
        // 방장을 제외한 모든 유저가 준비되었는지 확인
        List<GameJoinDTO> players = gameJoinService.getArrangeUserByTurn(gameJoinVO.getGameRoomId());
        boolean allReady = players.stream()
                .filter(p -> !p.getUserId().equals(gameJoinVO.getUserId())) // 방장 제외
                .allMatch(p -> p.getGameJoinIsReady() != null && p.getGameJoinIsReady() == 1);
        
        if (!allReady) {
            log.warn("Not all players are ready. gameRoomId: {}", gameJoinVO.getGameRoomId());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("type", "GAME_START_FAILED");
            errorResponse.put("message", "모든 플레이어가 준비되지 않았습니다.");
            errorResponse.put("gameState", players);
            
            simpMessagingTemplate.convertAndSend(
                    "/sub/game/snake/room/" + gameJoinVO.getGameRoomId(),
                    errorResponse
            );
            return;
        }
        
        // 비즈니스 로직은 여기에 작성
        gameJoinService.updateAllUserTurn(gameJoinVO.getGameRoomId());
        gameJoinService.updateUserTurn(gameJoinVO.getUserId());
        
        // 게임 상태 조회
        List<GameJoinDTO> gameState = gameJoinService.getArrangeUserByTurn(gameJoinVO.getGameRoomId());
        
        Map<String, Object> response = new HashMap<>();
        response.put("type", "GAME_STARTED");
        response.put("gameState", gameState);
        
        // 브로드캐스트
        simpMessagingTemplate.convertAndSend(
                "/sub/game/snake/room/" + gameJoinVO.getGameRoomId(),
                response
        );
    }

    /**
     * 주사위 굴리기 (모든 게임 로직 처리)
     * /pub/game/snake/roll-dice
     * 요청: { gameRoomId: Long, userId: Long }
     * 
     * 이 메서드에서 처리할 로직:
     * 1. 턴 검증 (getUserTurn)
     * 2. 현재 포지션 조회 (getUserPosition)
     * 3. 주사위 굴리기 (랜덤 2개)
     * 4. 새 포지션 계산 (현재 포지션 + 주사위 합)
     * 5. 함정/지름길 처리 (케이스문으로 처리)
     * 6. 포지션 업데이트 (updateUserPosition)
     * 7. 게임 종료 체크 (포지션 >= 100)
     * 8. 더블 체크 (같은 숫자면 턴 유지, 다르면 턴 넘기기)
     * 9. 턴 넘기기 (getArrangeUserByTurn으로 다음 유저 찾아서 updateUserTurn)
     */
    @MessageMapping("/game/snake/roll-dice")
    public void rollDice(GameJoinVO gameJoinVO) {
        log.info("Dice roll requested. gameRoomId: {}, userId: {}", 
                gameJoinVO.getGameRoomId(), gameJoinVO.getUserId());
        
        // ========== 비즈니스 로직 작성 영역 ==========
        
        // 1. 턴 검증
         boolean isMyTurn = gameJoinService.getUserTurn(gameJoinVO.getUserId());
         if (!isMyTurn) { return; }
        
        // 2. 게임방 정보 조회 (팀전 여부 확인)
        GameRoomDTO gameRoom = gameRoomService.getRoom(gameJoinVO.getGameRoomId());
        boolean isTeamMode = gameRoom.isGameRoomIsTeam();
        
        // 3. 현재 포지션 조회 (팀전이면 팀의 포지션, 개인전이면 개인 포지션)
        Integer currentPosition;
        String userTeamColor = null;
        if (isTeamMode) {
            // 팀전: 현재 유저의 팀 컬러 확인
            List<GameJoinDTO> players = gameJoinService.getArrangeUserByTurn(gameJoinVO.getGameRoomId());
            GameJoinDTO currentPlayer = players.stream()
                    .filter(p -> p.getUserId().equals(gameJoinVO.getUserId()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Player not found"));
            userTeamColor = currentPlayer.getGameJoinTeamcolor();
            // 같은 팀의 첫 번째 플레이어 포지션 사용 (팀 공유 포지션)
            final String teamColor = userTeamColor; // effectively final 변수 생성
            currentPosition = players.stream()
                    .filter(p -> teamColor != null && teamColor.equals(p.getGameJoinTeamcolor()))
                    .map(GameJoinDTO::getGameJoinPosition)
                    .findFirst()
                    .orElse(0);
        } else {
            // 개인전: 개인 포지션
            currentPosition = gameJoinService.getUserPosition(gameJoinVO.getUserId());
        }
        
        // 4. 주사위 굴리기 (랜덤 1~6 두 개)
         Random random = new Random();
         int dice1 = random.nextInt(6) + 1;
         int dice2 = random.nextInt(6) + 1;
        
        // 5. 새 포지션 계산
         int newPosition = currentPosition + dice1 + dice2;
        
        // 6. 함정/지름길 처리
         String boardType = null; // "TRAP" 또는 "LADDER"
         switch (newPosition) {
             // 뱀 (함정) - 아래로 내려감
             case 99:
                 newPosition = 65;
                 boardType = "TRAP";
                 break;
             case 95:
                 newPosition = 75;
                 boardType = "TRAP";
                 break;
             case 87:
                 newPosition = 24;
                 boardType = "TRAP";
                 break;
             case 64:
                 newPosition = 43;
                 boardType = "TRAP";
                 break;
             case 59:
                 newPosition = 2;
                 boardType = "TRAP";
                 break;
             case 36:
                 newPosition = 6;
                 boardType = "TRAP";
                 break;
             case 28:
                 newPosition = 10;
                 boardType = "TRAP";
                 break;
             case 16:
                 newPosition = 3;
                 boardType = "TRAP";
                 break;
             // 사다리 (지름길) - 위로 올라감
             case 4:
                 newPosition = 25;
                 boardType = "LADDER";
                 break;
             case 27:
                 newPosition = 48;
                 boardType = "LADDER";
                 break;
             case 33:
                 newPosition = 63;
                 boardType = "LADDER";
                 break;
             case 42:
                 newPosition = 60;
                 boardType = "LADDER";
                 break;
             case 50:
                 newPosition = 69;
                 boardType = "LADDER";
                 break;
             case 62:
                 newPosition = 81;
                 boardType = "LADDER";
                 break;
             case 74:
                 newPosition = 92;
                 boardType = "LADDER";
                 break;
         }
        
        // 7. 포지션 업데이트 (팀전이면 같은 팀의 모든 플레이어 포지션 업데이트)
         if (isTeamMode && userTeamColor != null) {
             // 팀전: 같은 팀의 모든 플레이어 포지션 업데이트
             List<GameJoinDTO> players = gameJoinService.getArrangeUserByTurn(gameJoinVO.getGameRoomId());
             final String teamColor = userTeamColor; // effectively final 변수 생성
             List<GameJoinDTO> teamPlayers = players.stream()
                     .filter(p -> teamColor.equals(p.getGameJoinTeamcolor()))
                     .collect(Collectors.toList());
             
             for (GameJoinDTO teamPlayer : teamPlayers) {
                 GameJoinVO teamPlayerVO = new GameJoinVO();
                 teamPlayerVO.setUserId(teamPlayer.getUserId());
                 teamPlayerVO.setGameRoomId(gameJoinVO.getGameRoomId());
                 teamPlayerVO.setGameJoinPosition(newPosition);
                 gameJoinService.updateUserPosition(teamPlayerVO);
             }
             log.info("Team position updated. teamColor: {}, newPosition: {}, teamSize: {}", 
                     userTeamColor, newPosition, teamPlayers.size());
         } else {
             // 개인전: 개인 포지션만 업데이트
             gameJoinVO.setGameJoinPosition(newPosition);
             gameJoinService.updateUserPosition(gameJoinVO);
         }
        
        // 8. 게임 종료 체크
         boolean gameEnded = newPosition >= 100;
         if (gameEnded) {
             gameJoinService.updateAllUserTurn(gameJoinVO.getGameRoomId());
             // 경험치 추가 등
         }
        
        // 9. 더블 체크 및 턴 넘기기
         boolean isDouble = dice1 == dice2;
         if (!isDouble && !gameEnded) {
             // 턴 넘기기 로직
              List<GameJoinDTO> players = gameJoinService.getArrangeUserByTurn(gameJoinVO.getGameRoomId());
             // 현재 유저 턴 0으로, 다음 유저 턴 1로
         }
        
        // ============================================
        
        // 게임 상태 조회
        List<GameJoinDTO> gameState = gameJoinService.getArrangeUserByTurn(gameJoinVO.getGameRoomId());
        
        Map<String, Object> response = new HashMap<>();
        response.put("type", "DICE_ROLLED");
        response.put("gameState", gameState);
         response.put("dice1", dice1);
         response.put("dice2", dice2);
         response.put("isDouble", isDouble);
         response.put("gameEnded", gameEnded);
         response.put("boardType", boardType); // "TRAP" 또는 "LADDER" 또는 null
         response.put("newPosition", newPosition);
        
        // 브로드캐스트
        simpMessagingTemplate.convertAndSend(
                "/sub/game/snake/room/" + gameJoinVO.getGameRoomId(),
                response
        );
    }

    /**
     * 게임 상태 조회 (선택적)
     * /pub/game/snake/state
     * 요청: { gameRoomId: Long }
     */
    @MessageMapping("/game/snake/state")
    public void getGameState(GameJoinVO gameJoinVO) {
        log.info("Game state requested. gameRoomId: {}", gameJoinVO.getGameRoomId());
        
        // 게임 상태 조회
        List<GameJoinDTO> gameState = gameJoinService.getArrangeUserByTurn(gameJoinVO.getGameRoomId());
        
        Map<String, Object> response = new HashMap<>();
        response.put("type", "GAME_STATE");
        response.put("gameState", gameState);
        
        // 브로드캐스트
        simpMessagingTemplate.convertAndSend(
                "/sub/game/snake/room/" + gameJoinVO.getGameRoomId(),
                response
        );
    }
}

