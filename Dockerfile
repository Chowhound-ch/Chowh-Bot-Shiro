# 从用户指定的 OpenJDK 镜像提取 JDK，并将证书文件复制为普通文件。
FROM openjdk:21 AS java-source
RUN mkdir -p /opt/java/openjdk && \
    cp -a "$JAVA_HOME/." /opt/java/openjdk/ && \
    cp -L "$JAVA_HOME/lib/security/cacerts" /tmp/java-cacerts && \
    rm -f /opt/java/openjdk/lib/security/cacerts && \
    cp /tmp/java-cacerts /opt/java/openjdk/lib/security/cacerts

# Ubuntu 24.04 + Java 21 + Python 3.12
FROM ubuntu:24.04 AS runtime-base
ENV DEBIAN_FRONTEND=noninteractive \
    JAVA_HOME=/opt/java/openjdk \
    PATH="/opt/java/openjdk/bin:$PATH" \
    TZ=Asia/Shanghai \
    LANG=en_US.UTF-8 \
    PLAYWRIGHT_BROWSERS_PATH=/ms-playwright

COPY --from=java-source /opt/java/openjdk/ /opt/java/openjdk/

RUN apt-get update && \
    apt-get install -y --no-install-recommends \
        ca-certificates locales language-pack-zh-hans fonts-noto-cjk \
        python3.12 python3.12-venv python3.12-dev python3-pip && \
    locale-gen en_US.UTF-8 && \
    ln -sf /usr/bin/python3.12 /usr/bin/python3 && \
    ln -sf /usr/bin/python3 /usr/bin/python && \
    java -version && \
    python3 --version && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# 在镜像内使用项目自带的 Gradle Wrapper 打包。
FROM runtime-base AS build
WORKDIR /workspace
COPY gradlew build.gradle.kts settings.gradle.kts ./
COPY gradle/ ./gradle/
COPY src/ ./src/
COPY config/ ./config/
RUN sed -i 's/\r$//' gradlew && \
    sh ./gradlew --no-daemon bootJar

FROM runtime-base AS runtime
WORKDIR /opt/chowh-bot
COPY --from=build /workspace/build/libs/ /opt/chowh-bot/
COPY start.sh /usr/local/bin/start-bot

# 与项目依赖版本匹配的 Chromium 和系统库一并安装进镜像。
# 构建产物暂存于 /opt/chowh-bot，启动时复制到挂载的 /app。
# 浏览器存放在挂载目录之外。
RUN java -cp 'lib/*' com.microsoft.playwright.CLI install --with-deps chromium && \
    chmod -R a+rX /ms-playwright && \
    sed -i 's/\r$//' /usr/local/bin/start-bot && \
    chmod +x /usr/local/bin/start-bot && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

ENV PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD=1 \
    BOT_APP_HOME=/opt/chowh-bot
WORKDIR /app
ENTRYPOINT ["/usr/local/bin/start-bot"]
