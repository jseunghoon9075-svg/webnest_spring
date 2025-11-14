#!/bin/bash

# 게임 API 테스트 스크립트
# 사용법: ./test_api.sh

BASE_URL="http://localhost:10000"
TOKEN=""  # 로그인 후 받은 토큰을 여기에 넣으세요

echo "=== 게임 API 테스트 ==="
echo ""

# 1. 게임방 목록 조회
echo "1. 게임방 목록 조회"
curl -X GET "${BASE_URL}/game-rooms/" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" | jq .
echo ""
echo ""

# 2. 특정 게임방 조회 (ID=1)
echo "2. 게임방 상세 조회 (ID=1)"
curl -X GET "${BASE_URL}/game-rooms/1" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" | jq .
echo ""
echo ""

# 3. 로그인 (테스트용)
echo "3. 로그인"
LOGIN_RESPONSE=$(curl -s -X POST "${BASE_URL}/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "userEmail": "test1@test.com",
    "userPassword": "test123!@#"
  }')

echo "$LOGIN_RESPONSE" | jq .
TOKEN=$(echo "$LOGIN_RESPONSE" | jq -r '.data.accessToken // empty')
echo ""
echo "토큰: $TOKEN"
echo ""

# WebSocket 테스트는 별도로 필요합니다.
echo "WebSocket 테스트는 game_test.html 파일을 브라우저에서 열어주세요."

