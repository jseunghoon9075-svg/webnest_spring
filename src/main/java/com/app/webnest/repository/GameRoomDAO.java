package com.app.webnest.repository;

import com.app.webnest.domain.dto.GameRoomDTO;
import com.app.webnest.domain.vo.GameRoomVO;
import com.app.webnest.mapper.GameRoomMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class GameRoomDAO {

    private final GameRoomMapper gameRoomMapper;

    public List<GameRoomDTO> getRooms() {
        return gameRoomMapper.selectAll();
    }

    public Optional<GameRoomDTO> getRoom(Long id) {
        return gameRoomMapper.select(id);
    }
    
    public void save(GameRoomVO gameRoomVO) {
        gameRoomMapper.insert(gameRoomVO);
    }
    
    public void delete(Long id) {
        gameRoomMapper.delete(id);
    }
    
    public void update(GameRoomVO gameRoomVO) {
        gameRoomMapper.update(gameRoomVO);
    }

}
