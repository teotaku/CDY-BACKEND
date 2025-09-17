#!/bin/bash

# 1) 기존 컨테이너/네트워크 내리기
docker compose -f docker-compose.dev.yml down

# 2) jar 새로 빌드 (테스트 제외)
./gradlew clean build -x test

# 3) 새 이미지 빌드 + 컨테이너 실행
docker compose -f docker-compose.dev.yml up --build -d
