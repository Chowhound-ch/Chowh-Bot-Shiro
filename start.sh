#!/bin/bash

set -e

APP_HOME="${BOT_APP_HOME:-.}"

# 将镜像自带的默认资源补充到挂载目录，已有配置文件优先。
if [ -n "${BOT_APP_HOME:-}" ]; then
    mkdir -p ./config
    cp -Rn "$APP_HOME/config/." ./config/
fi

#pip install -r ./config/scripts/python/jmcomic/requirements.txt  --break-system-packages

exec java -jar "$APP_HOME/Chowh-Bot-Shiro-0.0.1.jar" "$@"

