package com.app.webnest.service;

import com.app.webnest.domain.dto.GameJoinDTO;
import com.app.webnest.domain.vo.GameJoinVO;
import com.app.webnest.repository.GameJoinDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class GameJoinServiceImpl implements GameJoinService {

    private final GameJoinDAO gameJoinDAO;

    @Override
    public void join(GameJoinVO gameJoinVO) {
        gameJoinDAO.save(gameJoinVO);
    }

    @Override
    public void leave(GameJoinVO gameJoinVO) {
        gameJoinDAO.delete(gameJoinVO);
    }

    @Override
    public void updateTeamColor(GameJoinVO gameJoinVO) {
        gameJoinDAO.updateTeamColor(gameJoinVO);
    }

    @Override
    public List<GameJoinDTO> getPlayers(Long gameRoomId) {
        return gameJoinDAO.getPlayers(gameRoomId);
    }

    @Override
    public void update(GameJoinVO gameJoinVO){
        gameJoinDAO.save(gameJoinVO);
    }

    @Override
    public Optional<GameJoinVO> getGameJoinDTOByGameRoomId(GameJoinVO gameJoinVO){
        return gameJoinDAO.findUserInGameRoom(gameJoinVO);
    };

    @Override
    public List<GameJoinDTO> getArrangeUserByTurn(Long gameRoomId){
       return gameJoinDAO.findUserListByGameRoomId(gameRoomId);
    }
    @Override
    public Integer getUserPosition(Long userId) {
        return gameJoinDAO.findUserPosition(userId);
    }
    @Override
    public boolean getUserTurn(Long userId) {
        boolean result = false;
        if(gameJoinDAO.findUserTurn(userId) == 1){
            result = true;
        }
        return result;
    }
    @Override
    public void updateUserPosition(GameJoinVO gameJoinVO) {
        gameJoinDAO.modifyUserPosition(gameJoinVO);
    }
    @Override
    public void updateUserTurn(Long userId) {
        gameJoinDAO.modifyUserTurn(userId);
    }
    @Override
    public void updateAllUserTurn(Long gameRoomId){
        gameJoinDAO.modifyAllTurn(gameRoomId);
    }

    @Override
    public void updateReady(GameJoinVO gameJoinVO) {
        gameJoinDAO.updateReady(gameJoinVO);
    }
}
