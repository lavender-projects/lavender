#!/bin/bash

set -e

cd $(dirname "$0")/..
PROJECT_PATH="$(pwd)"

# 打包app/web模块
chmod +x ./app/scripts/*.sh
./app/scripts/deploy-web.sh

# Gradle打包
chmod +x gradlew
./gradlew build