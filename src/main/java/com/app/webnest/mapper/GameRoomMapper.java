package com.app.webnest.mapper;

import com.app.webnest.domain.dto.GameRoomDTO;
import com.app.webnest.domain.vo.GameRoomVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface GameRoomMapper {

    // 게임방 전체 조회
    public List<GameRoomDTO> selectAll();

    // 게임방 단일 조회
    public Optional<GameRoomDTO> select(Long id);
    
    // 게임방 생성
    public void insert(GameRoomVO gameRoomVO);
    
    // 게임방 삭제
    public void delete(Long id);
    
    // 게임방 수정
    public void update(GameRoomVO gameRoomVO);

}
