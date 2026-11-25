#!/bin/bash

set -euo pipefail
cd -- "$(dirname -- "${BASH_SOURCE[0]}")"

command -v java >/dev/null
docker compose version >/dev/null

# 在宿主机使用 Java 21 和项目自带的 Gradle Wrapper 打包。
sh ./gradlew --no-daemon bootJar
test -f ./build/libs/Chowh-Bot-Shiro-0.0.1.jar
test -d ./build/libs/lib
test -d ./build/libs/config

# 先构建运行环境，成功后再停止旧容器、更新应用文件。
docker compose build chowh-bot-shiro
docker compose stop chowh-bot-shiro

mkdir -p ./app/lib ./app/config
find ./app/lib -maxdepth 1 -type f -name '*.jar' -delete
cp -f ./build/libs/Chowh-Bot-Shiro-0.0.1.jar ./app/
cp -R ./build/libs/lib/. ./app/lib/
cp -Rn ./build/libs/config/. ./app/config/
sed 's/\r$//' ./start.sh > ./app/start.sh
chmod +x ./app/start.sh

docker compose up -d --no-build --force-recreate chowh-bot-shiro
