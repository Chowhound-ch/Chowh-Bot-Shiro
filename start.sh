#!/bin/bash

set -e

APP_HOME="${BOT_APP_HOME:-.}"

# 将镜像中的构建产物复制到挂载目录，已有配置文件和数据库优先。
if [ -n "${BOT_APP_HOME:-}" ]; then
    test -f "$APP_HOME/Chowh-Bot-Shiro-0.0.1.jar"
    test -d "$APP_HOME/lib"
    mkdir -p ./lib ./config
    # 清理旧版本依赖，避免不同版本的依赖同时被加载。
    find ./lib -maxdepth 1 -type f -name '*.jar' -delete
    cp -f "$APP_HOME/Chowh-Bot-Shiro-0.0.1.jar" ./
    cp -R "$APP_HOME/lib/." ./lib/
    cp -Rn "$APP_HOME/config/." ./config/
fi

#pip install -r ./config/scripts/python/jmcomic/requirements.txt  --break-system-packages

exec java -jar ./Chowh-Bot-Shiro-0.0.1.jar "$@"

