// React 예제: 게임 WebSocket 구독 및 상태 업데이트

import { useEffect, useState } from 'react';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';

function GameComponent({ gameRoomId, userId }) {
    const [gameState, setGameState] = useState([]);
    const [stompClient, setStompClient] = useState(null);
    const [diceResult, setDiceResult] = useState(null);

    useEffect(() => {
        // WebSocket 연결
        const socket = new SockJS('http://localhost:10000/ws');
        const client = new Client({
            webSocketFactory: () => socket,
            reconnectDelay: 5000,
            heartbeatIncoming: 4000,
            heartbeatOutgoing: 4000,
            onConnect: () => {
                console.log('WebSocket 연결 성공');
                
                // 게임방 구독
                client.subscribe(`/sub/game/snake/room/${gameRoomId}`, (message) => {
                    const data = JSON.parse(message.body);
                    console.log('게임 메시지 수신:', data);
                    
                    // 메시지 타입에 따라 처리
                    switch (data.type) {
                        case 'DICE_ROLLED':
                            // 주사위 굴리기 결과 처리
                            handleDiceRolled(data);
                            break;
                        case 'GAME_STARTED':
                            // 게임 시작 처리
                            handleGameStarted(data);
                            break;
                        case 'READY_UPDATED':
                            // 준비 상태 업데이트 처리
                            handleReadyUpdated(data);
                            break;
                        case 'GAME_STATE':
                            // 게임 상태 조회 결과 처리
                            handleGameState(data);
                            break;
                        case 'NOT_YOUR_TURN':
                            // 턴이 아닐 때 처리
                            alert('현재 당신의 턴이 아닙니다.');
                            break;
                        case 'DICE_ROLL_ERROR':
                            // 에러 처리
                            alert('주사위 굴리기 오류: ' + data.message);
                            break;
                        default:
                            console.log('알 수 없는 메시지 타입:', data.type);
                    }
                });
            },
            onStompError: (frame) => {
                console.error('STOMP 에러:', frame);
            },
        });

        client.activate();
        setStompClient(client);

        // 컴포넌트 언마운트 시 연결 해제
        return () => {
            if (client && client.active) {
                client.deactivate();
            }
        };
    }, [gameRoomId]);

    // 주사위 굴리기 결과 처리
    const handleDiceRolled = (data) => {
        // 주사위 결과 저장
        setDiceResult({
            dice1: data.dice1,
            dice2: data.dice2,
            isDouble: data.isDouble,
            newPosition: data.newPosition,
            boardType: data.boardType, // "TRAP" 또는 "LADDER" 또는 null
            gameEnded: data.gameEnded
        });

        // 게임 상태 업데이트 (모든 플레이어의 위치가 포함됨)
        if (data.gameState) {
            setGameState(data.gameState);
            
            // 화면에 플레이어 위치 반영
            updatePlayerPositions(data.gameState);
        }

        // 함정/지름길 알림
        if (data.boardType === 'TRAP') {
            alert(`뱀을 만났습니다! ${data.newPosition}번 칸으로 이동합니다.`);
        } else if (data.boardType === 'LADDER') {
            alert(`사다리를 만났습니다! ${data.newPosition}번 칸으로 이동합니다.`);
        }

        // 게임 종료 체크
        if (data.gameEnded) {
            alert('게임이 종료되었습니다!');
        }
    };

    // 게임 시작 처리
    const handleGameStarted = (data) => {
        if (data.gameState) {
            setGameState(data.gameState);
        }
        alert('게임이 시작되었습니다!');
    };

    // 준비 상태 업데이트 처리
    const handleReadyUpdated = (data) => {
        if (data.gameState) {
            setGameState(data.gameState);
        }
    };

    // 게임 상태 조회 결과 처리
    const handleGameState = (data) => {
        if (data.gameState) {
            setGameState(data.gameState);
            updatePlayerPositions(data.gameState);
        }
    };

    // 플레이어 위치를 화면에 반영
    const updatePlayerPositions = (players) => {
        players.forEach(player => {
            // player.userId와 player.gameJoinPosition을 사용하여
            // 게임 보드에서 해당 플레이어의 말을 이동시킴
            console.log(`플레이어 ${player.userId}의 위치: ${player.gameJoinPosition}`);
            
            // 예: 게임 보드 컴포넌트에 위치 전달
            // movePlayerPiece(player.userId, player.gameJoinPosition);
        });
    };

    // 주사위 굴리기 요청
    const rollDice = () => {
        if (stompClient && stompClient.connected) {
            stompClient.publish({
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
        if (stompClient && stompClient.connected) {
            stompClient.publish({
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
        if (stompClient && stompClient.connected) {
            stompClient.publish({
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
            <h2>게임 상태</h2>
            
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

            {/* 주사위 결과 */}
            {diceResult && (
                <div>
                    <h3>주사위 결과</h3>
                    <p>주사위 1: {diceResult.dice1}</p>
                    <p>주사위 2: {diceResult.dice2}</p>
                    <p>합계: {diceResult.dice1 + diceResult.dice2}</p>
                    {diceResult.isDouble && <p>더블! 한 번 더 굴릴 수 있습니다.</p>}
                    {diceResult.boardType === 'TRAP' && <p>⚠️ 뱀을 만났습니다!</p>}
                    {diceResult.boardType === 'LADDER' && <p>🎯 사다리를 만났습니다!</p>}
                </div>
            )}

            {/* 게임 액션 버튼 */}
            <div>
                <button onClick={updateReady}>준비하기</button>
                <button onClick={startGame}>게임 시작</button>
                <button onClick={rollDice}>주사위 굴리기</button>
            </div>
        </div>
    );
}

export default GameComponent;

