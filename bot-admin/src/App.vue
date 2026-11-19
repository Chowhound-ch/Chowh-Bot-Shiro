<script setup lang="ts">
import { onMounted, onUnmounted, ref } from 'vue'
import { RouterLink, RouterView } from 'vue-router'
import { request, setSession, type Session } from './api'

const session = ref<Session>()
const checking = ref(true)
const busy = ref(false)
const userId = ref('')
const password = ref('')
const error = ref('')
const navigation = [
  { path: 'overview', icon: '◫', name: '运行概览' },
  { path: 'dictionary', icon: '▤', name: '数据字典' },
  { path: 'kfc', icon: '▧', name: 'KFC 文案' },
  { path: 'subscriptions', icon: '◉', name: '订阅管理' },
  { path: 'logs', icon: '≡', name: '调用日志' },
]
function expire() { session.value = undefined; setSession() }
async function login() {
  busy.value = true
  error.value = ''
  try {
    const result = await request<Session>('/system/login', 'POST', { userId: userId.value, password: password.value })
    session.value = result
    setSession(result)
    password.value = ''
  } catch (e) { error.value = (e as Error).message }
  finally { busy.value = false }
}
async function logout() {
  try { await request('/api/admin/logout', 'POST'); expire() }
  catch (e) { error.value = (e as Error).message }
}
onMounted(async () => {
  window.addEventListener('session-expired', expire)
  try { session.value = await request<Session>('/api/admin/session'); setSession(session.value) }
  catch { expire() }
  finally { checking.value = false }
})
onUnmounted(() => window.removeEventListener('session-expired', expire))
</script>

<template>
  <div v-if="checking" class="initial-loading">正在连接管理服务…</div>
  <main v-else-if="!session" class="login-page">
    <section class="login-intro">
      <div class="brand"><span class="brand-mark">C</span> Chowh Bot</div>
      <div><p class="eyebrow">CHOWH BOT · ADMIN</p><h1>让日常管理<br>井然有序。</h1><p>维护字典、管理内容和订阅，查看机器人的每次调用。</p></div>
      <small>Chowh Bot 管理控制台</small>
    </section>
    <form class="login-form" @submit.prevent="login">
      <h2>登录控制台</h2><p class="muted">使用已有的管理员 QQ 账号和密码</p>
      <label>QQ 号<input v-model="userId" inputmode="numeric" pattern="[0-9]+" autocomplete="username" required placeholder="请输入 QQ 号"></label>
      <label>密码<input v-model="password" type="password" autocomplete="current-password" required placeholder="请输入密码"></label>
      <p v-if="error" class="error" role="alert">{{ error }}</p>
      <button class="primary" :disabled="busy">{{ busy ? '正在登录…' : '登录' }}</button>
    </form>
  </main>
  <div v-else class="app-shell">
    <aside class="sidebar">
      <RouterLink to="/overview" class="brand"><span class="brand-mark">C</span><span>Chowh Bot<small>管理控制台</small></span></RouterLink>
      <p class="nav-label">工作空间</p>
      <nav><RouterLink v-for="item in navigation" :key="item.path" :to="'/' + item.path"><span class="nav-icon" aria-hidden="true">{{ item.icon }}</span>{{ item.name }}</RouterLink></nav>
      <div class="sidebar-bottom"><span class="avatar">管</span><div>管理员<small>{{ session.userId }}</small></div><button title="退出登录" aria-label="退出登录" @click="logout">↪</button></div>
    </aside>
    <div class="workspace">
      <header class="topbar"><span>工作空间 <span class="slash">/</span> 管理控制台</span><span class="workspace-name"><span class="tiny-dot"></span>Chowh Bot</span></header>
      <p v-if="error" class="error global-error" role="alert">{{ error }}</p>
      <RouterView v-slot="{ Component, route }"><component :is="Component" :key="route.path" /></RouterView>
    </div>
  </div>
</template>
