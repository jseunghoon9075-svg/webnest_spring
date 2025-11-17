package com.app.webnest.api.privateapi.game.websocket;

import com.app.webnest.domain.dto.GameJoinDTO;
import com.app.webnest.domain.vo.GameJoinVO;
import com.app.webnest.service.GameJoinService;
import com.app.webnest.service.GameRoomService;
import com.app.webnest.service.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CardFlipApi {
    private final GameJoinService gameJoinService;
    private final GameRoomService gameRoomService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final UserServiceImpl userService;

    @MessageMapping("/game/card-flip/ready")
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
                "/sub/game/card-flip/room/" + gameJoinVO.getGameRoomId(),
                response
        );
    }

    @MessageMapping("/game/card-flip/start")
    public void startGame(GameJoinVO gameJoinVO) {

        // 게임 시작 시 모든 플레이어를 자동으로 준비완료 상태로 변경
        List<GameJoinDTO> players = gameJoinService.getArrangeUserByTurn(gameJoinVO.getGameRoomId());

        // 모든 플레이어를 준비완료 상태로 변경
        for (GameJoinDTO player : players) {
            GameJoinVO readyVO = new GameJoinVO();
            readyVO.setUserId(player.getUserId());
            readyVO.setGameRoomId(gameJoinVO.getGameRoomId());
            readyVO.setGameJoinIsReady(1);
            gameJoinService.updateReady(readyVO);
        }

        // 게임 시작 전 초기화 (안전장치)
        // 포지션이 0이 아닌 경우를 대비해 모든 포지션을 0으로 초기화
        gameJoinService.resetAllPosition(gameJoinVO.getGameRoomId());

        // 모든 턴을 0으로 설정
        gameJoinService.updateAllUserTurn(gameJoinVO.getGameRoomId());

        // 게임 상태를 다시 조회하여 최신 상태 가져오기
        List<GameJoinDTO> currentPlayers = gameJoinService.getArrangeUserByTurn(gameJoinVO.getGameRoomId());

        // 첫 번째 플레이어의 턴을 1로 설정
        if (!currentPlayers.isEmpty()) {
            GameJoinVO firstPlayerVO = new GameJoinVO();
            firstPlayerVO.setUserId(currentPlayers.get(0).getUserId());
            firstPlayerVO.setGameRoomId(gameJoinVO.getGameRoomId());
            gameJoinService.updateUserTurn(firstPlayerVO);
            log.info("First player turn set. userId: {}, gameRoomId: {}",
                    currentPlayers.get(0).getUserId(), gameJoinVO.getGameRoomId());
        } else {
            log.warn("No players found to set turn. gameRoomId: {}", gameJoinVO.getGameRoomId());
        }

        // 게임 상태 조회 (턴 설정 후)
        List<GameJoinDTO> gameState = gameJoinService.getArrangeUserByTurn(gameJoinVO.getGameRoomId());
        log.info("Game state after turn set. Players count: {}", gameState.size());
        gameState.forEach(p -> {
            log.info("Player in gameState - userId: {}, myTurn: {}", p.getUserId(), p.isGameJoinMyturn());
        });

        Map<String, Object> response = new HashMap<>();
        response.put("type", "GAME_STARTED");
        response.put("gameState", gameState);

        // 브로드캐스트
        simpMessagingTemplate.convertAndSend(
                "/sub/game/card-flip/room/" + gameJoinVO.getGameRoomId(),
                response
        );
    }
    @MessageMapping("/game/card-flip/state")
    public void getGameState(GameJoinVO gameJoinVO) {
        log.info("Game state requested. gameRoomId: {}", gameJoinVO.getGameRoomId());

        // 게임 상태 조회
        List<GameJoinDTO> gameState = gameJoinService.getArrangeUserByTurn(gameJoinVO.getGameRoomId());

        Map<String, Object> response = new HashMap<>();
        response.put("type", "GAME_STATE");
        response.put("gameState", gameState);

        // 브로드캐스트
        simpMessagingTemplate.convertAndSend(
                "/sub/game/card-flip/room/" + gameJoinVO.getGameRoomId(),
                response
        );
    }
}
