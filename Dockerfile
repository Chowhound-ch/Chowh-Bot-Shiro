# 构建 Python
FROM openjdk:21 as openjdk-build
FROM ubuntu:24.04

# 从 Python 构建阶段复制文件
# 设置环境变量，避免交互式提示
ENV DEBIAN_FRONTEND=noninteractive

# 更新包列表并安装 Python 3.12
RUN apt-get update && \
    apt-get install -y python3.12 python3.12-venv python3.12-dev python3-pip && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# 创建符号链接（如果需要）
RUN ln -sf /usr/bin/python3.12 /usr/bin/python3 && \
    ln -sf /usr/bin/python3 /usr/bin/python

ENV JAVA_HOME /usr/java/openjdk-21
ENV PATH="$JAVA_HOME/bin:$PATH"
ENV TZ=Asia/Shanghai
RUN mkdir -p $JAVA_HOME
COPY --from=openjdk-build /usr/java/openjdk-21 /usr/java/openjdk-21
COPY --from=openjdk-build /etc/pki/ca-trust/extracted/java /etc/pki/ca-trust/extracted/java

RUN apt update
RUN apt install libc6

# 确保必要的符号链接

# 验证安装
RUN python3 --version && \
    pip3 --version
RUN java -version

WORKDIR /app
COPY /app/* /app/
WORKDIR /app/

RUN apt update
RUN apt install -y  language-pack-zh-hans
RUN locale-gen en_US.UTF-8
ENV LANG="en_US.UTF-8"

ENTRYPOINT ["/app/start.sh"]

