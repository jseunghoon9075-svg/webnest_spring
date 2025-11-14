// React 예제: 게임 상태 조회 및 useEffect 의존성 배열 활용

import { useEffect, useState } from 'react';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';
import axios from 'axios';

function GameRoomComponent({ gameRoomId, userId }) {
    const [gameState, setGameState] = useState([]);
    const [positions, setPositions] = useState({}); // { userId: position }
    const stompClientRef = useRef(null);

    // ========== 1. 초기 로드 시 게임 상태 조회 (REST API) ==========
    useEffect(() => {
        const fetchGameState = async () => {
            try {
                const response = await axios.get(
                    `http://localhost:10000/private/game-rooms/${gameRoomId}/game-state`,
                    {
                        headers: {
                            'Authorization': `Bearer ${yourAccessToken}` // JWT 토큰
                        }
                    }
                );
                
                const gameStateData = response.data.data; // ApiResponseDTO의 data 필드
                setGameState(gameStateData);
                
                // 포지션 맵 생성
                const positionMap = {};
                gameStateData.forEach(player => {
                    positionMap[player.userId] = player.gameJoinPosition || 0;
                });
                setPositions(positionMap);
                
                console.log('초기 게임 상태 로드:', gameStateData);
            } catch (error) {
                console.error('게임 상태 조회 실패:', error);
            }
        };

        fetchGameState();
    }, [gameRoomId]); // gameRoomId가 변경될 때마다 조회

    // ========== 2. WebSocket 구독 (실시간 업데이트) ==========
    useEffect(() => {
        const socket = new SockJS('http://localhost:10000/ws');
        const client = new Client({
            webSocketFactory: () => socket,
            reconnectDelay: 5000,
            onConnect: () => {
                console.log('WebSocket 연결 성공');
                
                // 게임 채널 구독
                client.subscribe(`/sub/game/snake/room/${gameRoomId}`, (message) => {
                    const data = JSON.parse(message.body);
                    console.log('게임 메시지 수신:', data);
                    
                    // 게임 상태 업데이트
                    if (data.gameState) {
                        setGameState(data.gameState);
                        
                        // 포지션 맵 업데이트
                        const newPositions = {};
                        data.gameState.forEach(player => {
                            newPositions[player.userId] = player.gameJoinPosition || 0;
                        });
                        setPositions(newPositions);
                    }
                });
            },
        });

        client.activate();
        stompClientRef.current = client;

        return () => {
            if (client && client.active) {
                client.deactivate();
            }
        };
    }, [gameRoomId]);

    // ========== 3. 포지션 변경 시 화면 업데이트 (useEffect 의존성 배열) ==========
    useEffect(() => {
        console.log('포지션 변경 감지:', positions);
        
        // positions 객체의 값이 변경될 때마다 실행됨
        // 여기서 게임 보드의 플레이어 말을 이동시킴
        Object.entries(positions).forEach(([userId, position]) => {
            console.log(`플레이어 ${userId}의 위치: ${position}`);
            // movePlayerPiece(userId, position); // 게임 보드 컴포넌트에 위치 전달
        });
    }, [positions]); // positions가 변경될 때마다 실행

    // 또는 특정 플레이어의 포지션만 감지하려면:
    // useEffect(() => {
    //     const playerPosition = positions[userId]; // 현재 유저의 포지션
    //     console.log('내 위치 변경:', playerPosition);
    // }, [positions[userId]]); // 특정 플레이어의 포지션만 감지

    return (
        <div>
            <h2>게임방 {gameRoomId}</h2>
            
            {/* 플레이어 목록 */}
            <div>
                <h3>플레이어 목록</h3>
                {gameState.map(player => (
                    <div key={player.id}>
                        <p>
                            {player.userNickname} 
                            (위치: {positions[player.userId] || 0})
                            {player.gameJoinIsTurn === 1 && ' ⭐ 현재 턴'}
                            {player.gameJoinIsReady === 1 && ' ✓ 준비됨'}
                        </p>
                    </div>
                ))}
            </div>
        </div>
    );
}

export default GameRoomComponent;

