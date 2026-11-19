# bot-admin

Chowh Bot 管理控制台，包含运行概览、数据字典、KFC 文案、订阅管理和调用日志。

先启动 Java 后端，再在本目录运行 `npm run dev`。开发服务器将 `/api` 和 `/system` 代理到 `http://localhost:8002`，可通过环境变量 `VITE_API_TARGET` 指定其他后端地址。

使用数据库 `user` 表里已有的管理员或群主账号登录，QQ 号对应 `user_id`，密码对应 `password`，角色 `role` 为 3 或 5。未设置密码的账号需要先设置密码。

`npm run build` 执行类型检查并生成 `dist/`。生产环境将 `dist/` 作为静态站点发布，并把同域的 `/api/` 和 `/system/` 转发到 Java 后端，转发时保留 `Authorization` 请求头。

后台使用 JWT Bearer Token，前端将 Token 保存在 `sessionStorage` 中。Redis 以 `chowh:admin:session:<Token ID>` 保存会话，JWT 和 Redis 会话默认 30 分钟到期，退出登录时删除对应 Redis 记录。后端需要配置 Redis 连接及 `BOT_JWT_SECRET`，密钥为至少 32 字节随机数的 Base64 编码，有效期可通过 `BOT_JWT_TOKEN_TTL` 调整。开发环境已在 `config/.env.dev` 中配置独立随机密钥。

This template should help get you started developing with Vue 3 in Vite.

## Recommended IDE Setup

[VS Code](https://code.visualstudio.com/) + [Vue (Official)](https://marketplace.visualstudio.com/items?itemName=Vue.volar) (and disable Vetur).

## Recommended Browser Setup

- Chromium-based browsers (Chrome, Edge, Brave, etc.):
  - [Vue.js devtools](https://chromewebstore.google.com/detail/vuejs-devtools/nhdogjmejiglipccpnnnanhbledajbpd)
  - [Turn on Custom Object Formatter in Chrome DevTools](http://bit.ly/object-formatters)
- Firefox:
  - [Vue.js devtools](https://addons.mozilla.org/en-US/firefox/addon/vue-js-devtools/)
  - [Turn on Custom Object Formatter in Firefox DevTools](https://fxdx.dev/firefox-devtools-custom-object-formatters/)

## Type Support for `.vue` Imports in TS

TypeScript cannot handle type information for `.vue` imports by default, so we replace the `tsc` CLI with `vue-tsc` for type checking. In editors, we need [Volar](https://marketplace.visualstudio.com/items?itemName=Vue.volar) to make the TypeScript language service aware of `.vue` types.

## Customize configuration

See [Vite Configuration Reference](https://vite.dev/config/).

## Project Setup

```sh
yarn
```

### Compile and Hot-Reload for Development

```sh
yarn dev
```

### Type-Check, Compile and Minify for Production

```sh
yarn build
```
