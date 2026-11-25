### 结构

### Docker 部署

宿主机需要 Java 21、Docker 和 Docker Compose。项目在宿主机通过 Gradle Wrapper 构建，Dockerfile 负责准备 Java、Python 和 Playwright 浏览器运行环境。

在项目根目录执行：

```bash
bash deploy.sh
```

脚本先执行 `sh ./gradlew --no-daemon bootJar`，再构建镜像、停止旧容器，将 JAR、依赖、默认配置和 `start.sh` 复制到 `app/`，最后重建并启动容器。更新时会清理旧依赖，已有配置和数据库会保留。

Compose 将 `./app` 挂载到容器的 `/app`，通过 `/bin/bash /app/start.sh` 启动。环境文件需放在 `app/config/.env.<profile>`（默认 profile 为 `dev`），数据库按 `BOT_DB_FILE` 配置放到对应位置。首次从旧部署迁移时，请先迁入这些文件。需要使用生产环境时，在 Compose 的 `environment` 中设置 `SPRING_PROFILES_ACTIVE=prod`。

Dockerfile 会读取宿主机构建生成的 `build/libs/lib/` 来安装对应版本的 Playwright 浏览器，因此请通过 `deploy.sh` 完成构建和启动。

### MCP 客户端

MCP 客户端默认关闭。连接 Streamable HTTP 服务时，在 `config/.env.dev`（或当前 profile 对应的 `.env` 文件）中设置以下变量，然后重启应用。

```dotenv
BOT_MCP_ENABLED=true
BOT_MCP_URL=https://chowhound.5ddd.com:9900
BOT_MCP_ENDPOINT=/mcp
BOT_MCP_API_KEY=填写完整的密钥
```

`BOT_MCP_URL` 填写服务的协议、主机和端口，`BOT_MCP_ENDPOINT` 填写以 `/` 开头的 MCP 路径。客户端通过 `X-API-Key` 请求头发送 `BOT_MCP_API_KEY`。启用后，应用会在启动时初始化同步客户端，请求超时为 20 秒。

如果访问 MCP 服务需要 HTTP 代理，在对应的 `.env` 中设置 `BOT_MCP_USE_PROXY=true`，并填写 `BOT_PROXY_HOST` 和 `BOT_PROXY_PORT`。
