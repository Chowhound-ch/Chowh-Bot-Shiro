#!/bin/bash

set -e

cd -- "$(dirname -- "${BASH_SOURCE[0]}")"

test -f ./Chowh-Bot-Shiro-0.0.1.jar
test -d ./lib

#pip install -r ./config/scripts/python/jmcomic/requirements.txt  --break-system-packages

exec java -jar ./Chowh-Bot-Shiro-0.0.1.jar "$@"

