package com.app.webnest.service;

import com.app.webnest.domain.dto.GameRoomDTO;
import com.app.webnest.domain.vo.GameRoomVO;
import com.app.webnest.domain.vo.GameJoinVO;

import java.util.List;

public interface GameRoomService {
    public List<GameRoomDTO> getRooms();
    
    public List<GameRoomDTO> getRooms(Long userId);

    public GameRoomDTO getRoom(Long id);
    
    public Long create(GameRoomVO gameRoomVO);
    
    /**
     * 게임방 생성과 호스트 추가를 한 트랜잭션에서 처리
     * @param gameRoomVO 게임방 정보
     * @param hostUserId 호스트 유저 ID
     * @return 생성된 게임방 DTO
     */
    public GameRoomDTO createRoomWithHost(GameRoomVO gameRoomVO, Long hostUserId);
    
    public void delete(Long id);
    
    public void update(GameRoomVO gameRoomVO);
}
