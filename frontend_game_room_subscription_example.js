// 게임방 입장 시 채팅 + 게임 채널 자동 구독 예제

import { useEffect, useState, useRef } from 'react';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';

function GameRoomComponent({ gameRoomId, userId }) {
    const [gameState, setGameState] = useState([]);
    const [chatMessages, setChatMessages] = useState([]);
    const stompClientRef = useRef(null);

    useEffect(() => {
        // WebSocket 연결 (게임방 입장 시 한 번만)
        const socket = new SockJS('http://localhost:10000/ws');
        const client = new Client({
            webSocketFactory: () => socket,
            reconnectDelay: 5000,
            heartbeatIncoming: 4000,
            heartbeatOutgoing: 4000,
            onConnect: () => {
                console.log('WebSocket 연결 성공 - 게임방 입장');
                
                // ========== 1. 채팅 채널 구독 ==========
                client.subscribe(`/sub/chats/room/${gameRoomId}`, (message) => {
                    const chatData = JSON.parse(message.body);
                    console.log('채팅 메시지 수신:', chatData);
                    
                    // 채팅 메시지 추가
                    setChatMessages(prev => [...prev, chatData]);
                });
                console.log(`채팅 채널 구독: /sub/chats/room/${gameRoomId}`);
                
                // ========== 2. 게임 채널 구독 (자동) ==========
                client.subscribe(`/sub/game/snake/room/${gameRoomId}`, (message) => {
                    const gameData = JSON.parse(message.body);
                    console.log('게임 메시지 수신:', gameData);
                    
                    // 메시지 타입에 따라 자동 처리
                    handleGameMessage(gameData);
                });
                console.log(`게임 채널 구독: /sub/game/snake/room/${gameRoomId}`);
                
                // ========== 3. 게임방 입장 알림 (JOIN 메시지 전송) ==========
                client.publish({
                    destination: '/pub/chats/send',
                    body: JSON.stringify({
                        chatMessageType: 'JOIN',
                        userSenderId: userId,
                        gameRoomId: gameRoomId,
                        userSenderTeamcolor: 'RED' // 또는 프론트에서 선택한 팀 컬러
                    })
                });
            },
            onStompError: (frame) => {
                console.error('STOMP 에러:', frame);
            },
        });

        client.activate();
        stompClientRef.current = client;

        // 컴포넌트 언마운트 시 연결 해제
        return () => {
            if (client && client.active) {
                client.deactivate();
            }
        };
    }, [gameRoomId, userId]);

    // 게임 메시지 자동 처리 함수
    const handleGameMessage = (data) => {
        switch (data.type) {
            case 'DICE_ROLLED':
                // 주사위 굴리기 결과 - 자동으로 화면 업데이트
                handleDiceRolled(data);
                break;
                
            case 'GAME_STARTED':
                // 게임 시작 - 자동으로 게임 상태 업데이트
                if (data.gameState) {
                    setGameState(data.gameState);
                }
                break;
                
            case 'READY_UPDATED':
                // 준비 상태 변경 - 자동으로 게임 상태 업데이트
                if (data.gameState) {
                    setGameState(data.gameState);
                }
                break;
                
            case 'GAME_STATE':
                // 게임 상태 조회 결과 - 자동으로 게임 상태 업데이트
                if (data.gameState) {
                    setGameState(data.gameState);
                    updatePlayerPositions(data.gameState);
                }
                break;
                
            case 'NOT_YOUR_TURN':
                // 턴이 아닐 때 (자신이 요청한 경우에만)
                console.warn('현재 당신의 턴이 아닙니다.');
                break;
                
            case 'DICE_ROLL_ERROR':
                // 에러 (자신이 요청한 경우에만)
                console.error('주사위 굴리기 오류:', data.message);
                break;
                
            default:
                console.log('알 수 없는 메시지 타입:', data.type);
        }
    };

    // 주사위 굴리기 결과 자동 처리
    const handleDiceRolled = (data) => {
        // 게임 상태 업데이트 (모든 플레이어의 위치 포함)
        if (data.gameState) {
            setGameState(data.gameState);
            
            // 화면에 플레이어 위치 자동 반영
            updatePlayerPositions(data.gameState);
        }

        // 주사위 결과 표시 (옵션)
        const rollingPlayer = data.gameState?.find(p => p.gameJoinPosition === data.newPosition);
        if (rollingPlayer) {
            console.log(`${rollingPlayer.userNickname}님이 주사위를 굴렸습니다!`);
            console.log(`주사위: ${data.dice1} + ${data.dice2} = ${data.dice1 + data.dice2}`);
            console.log(`새 위치: ${data.newPosition}`);
        }

        // 함정/지름길 알림
        if (data.boardType === 'TRAP') {
            console.log(`⚠️ 뱀을 만났습니다! ${data.newPosition}번 칸으로 이동합니다.`);
        } else if (data.boardType === 'LADDER') {
            console.log(`🎯 사다리를 만났습니다! ${data.newPosition}번 칸으로 이동합니다.`);
        }

        // 게임 종료 체크
        if (data.gameEnded) {
            console.log('🎉 게임이 종료되었습니다!');
        }
    };

    // 플레이어 위치를 화면에 자동 반영
    const updatePlayerPositions = (players) => {
        players.forEach(player => {
            // player.userId와 player.gameJoinPosition을 사용하여
            // 게임 보드에서 해당 플레이어의 말을 자동으로 이동시킴
            console.log(`플레이어 ${player.userNickname} (ID: ${player.userId})의 위치: ${player.gameJoinPosition}`);
            
            // 예: 게임 보드 컴포넌트에 위치 전달
            // movePlayerPiece(player.userId, player.gameJoinPosition);
            
            // 또는 상태로 관리하는 경우:
            // setPlayerPositions(prev => ({
            //     ...prev,
            //     [player.userId]: player.gameJoinPosition
            // }));
        });
    };

    // 주사위 굴리기 요청 (사용자가 버튼 클릭 시)
    const rollDice = () => {
        if (stompClientRef.current && stompClientRef.current.connected) {
            stompClientRef.current.publish({
                destination: `/pub/game/snake/roll-dice`,
                body: JSON.stringify({
                    gameRoomId: gameRoomId,
                    userId: userId
                })
            });
        }
    };

    // 준비하기 요청
    const updateReady = () => {
        if (stompClientRef.current && stompClientRef.current.connected) {
            stompClientRef.current.publish({
                destination: `/pub/game/snake/ready`,
                body: JSON.stringify({
                    gameRoomId: gameRoomId,
                    userId: userId,
                    gameJoinIsReady: 1
                })
            });
        }
    };

    // 게임 시작 요청
    const startGame = () => {
        if (stompClientRef.current && stompClientRef.current.connected) {
            stompClientRef.current.publish({
                destination: `/pub/game/snake/start`,
                body: JSON.stringify({
                    gameRoomId: gameRoomId,
                    userId: userId
                })
            });
        }
    };

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
                            (위치: {player.gameJoinPosition || 0})
                            {player.gameJoinIsTurn === 1 && ' ⭐ 현재 턴'}
                            {player.gameJoinIsReady === 1 && ' ✓ 준비됨'}
                        </p>
                    </div>
                ))}
            </div>

            {/* 채팅 메시지 */}
            <div>
                <h3>채팅</h3>
                {chatMessages.map((msg, idx) => (
                    <div key={idx}>
                        <strong>{msg.senderNickname}:</strong> {msg.chatMessageContent}
                    </div>
                ))}
            </div>

            {/* 게임 액션 버튼 */}
            <div>
                <button onClick={updateReady}>준비하기</button>
                <button onClick={startGame}>게임 시작</button>
                <button onClick={rollDice}>주사위 굴리기</button>
            </div>
        </div>
    );
}

export default GameRoomComponent;

