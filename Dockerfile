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

FROM runtime-base AS runtime
# 依赖由宿主机执行 Gradle Wrapper 生成，仅用于安装匹配版本的浏览器。
COPY build/libs/lib/ /tmp/playwright-lib/

# 与项目依赖版本匹配的 Chromium 和系统库一并安装进镜像。
# 宿主机的 ./app 提供应用文件，浏览器保存在挂载目录之外。
RUN java -cp '/tmp/playwright-lib/*' com.microsoft.playwright.CLI install --with-deps chromium && \
    chmod -R a+rX /ms-playwright && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/* /tmp/playwright-lib

ENV PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD=1
WORKDIR /app
ENTRYPOINT ["/bin/bash", "/app/start.sh"]
