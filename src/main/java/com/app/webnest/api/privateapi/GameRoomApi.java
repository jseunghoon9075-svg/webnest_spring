package com.app.webnest.api.privateapi;

import com.app.webnest.domain.dto.ApiResponseDTO;
import com.app.webnest.domain.dto.GameJoinDTO;
import com.app.webnest.domain.dto.GameRoomDTO;
import com.app.webnest.repository.GameRoomDAO;
import com.app.webnest.service.GameJoinService;
import com.app.webnest.service.GameRoomService;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/game-rooms/*")
public class GameRoomApi {

    private final GameRoomService gameRoomService;
    private final GameJoinService gameJoinService;

    @GetMapping("")
    public ResponseEntity<ApiResponseDTO<List<GameRoomDTO>>> getRooms() {
        List<GameRoomDTO> rooms = gameRoomService.getRooms();
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponseDTO.of("채팅방 목록조회", rooms));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<GameRoomDTO>> getRoom(@PathVariable Long id) {
        GameRoomDTO room = gameRoomService.getRoom(id);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponseDTO.of("채팅방 목록조회", room));
    }

    /**
     * 게임 상태 조회 (플레이어 위치, 턴, 레디 상태 등)
     * GET /private/game-rooms/{gameRoomId}/game-state
     * 채팅의 getChats처럼 초기 로드 시 사용
     */
    @GetMapping("/{gameRoomId}/game-state")
    public ResponseEntity<ApiResponseDTO<List<GameJoinDTO>>> getGameState(@PathVariable Long gameRoomId) {
        List<GameJoinDTO> gameState = gameJoinService.getArrangeUserByTurn(gameRoomId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponseDTO.of("게임 상태 조회 성공", gameState));
    }

}
