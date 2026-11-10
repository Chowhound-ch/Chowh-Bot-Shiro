### 结构

### MCP 客户端

MCP 客户端默认关闭。连接 Streamable HTTP 服务时，在 `config/.env.dev`（或当前 profile 对应的 `.env` 文件）中设置以下变量，然后重启应用。

```dotenv
BOT_MCP_ENABLED=true
BOT_MCP_URL=https://chowhound.5ddd.com:9900
BOT_MCP_ENDPOINT=/mcp
BOT_MCP_API_KEY=填写完整的密钥
```

`BOT_MCP_URL` 填写服务的协议、主机和端口，`BOT_MCP_ENDPOINT` 填写以 `/` 开头的 MCP 路径。客户端通过 `X-API-Key` 请求头发送 `BOT_MCP_API_KEY`。启用后，应用会在启动时初始化同步客户端，请求超时为 20 秒。
