package com.app.webnest.service;

import com.app.webnest.domain.dto.GameRoomDTO;
import com.app.webnest.domain.vo.GameRoomVO;

import java.util.List;

public interface GameRoomService {
    public List<GameRoomDTO> getRooms();
    
    public List<GameRoomDTO> getRooms(Long userId);

    public GameRoomDTO getRoom(Long id);
    
    public Long create(GameRoomVO gameRoomVO);
    
    public void delete(Long id);
    
    public void update(GameRoomVO gameRoomVO);
}
