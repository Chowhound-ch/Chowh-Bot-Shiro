<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { RouterLink, useRoute } from 'vue-router'
import { request, type Row, type PageData } from '../api'

const section = String(useRoute().params.section)
const titles: Record<string, string> = { overview: '运行概览', dictionary: '数据字典', kfc: 'KFC 文案', subscriptions: '订阅管理', logs: '调用日志' }
const descriptions: Record<string, string> = { overview: '机器人的运行情况与最近动态', dictionary: '维护字典分类、编码与名称', kfc: '维护随机发送的文字与 CQ 图片模板', subscriptions: '管理群聊和私聊的事件订阅', logs: '查看用户触发监听器的历史记录' }
const title = titles[section] ?? ''
const description = descriptions[section]
const loading = ref(false)
const saving = ref(false)
const error = ref('')
const success = ref('')
const rows = ref<Row[]>([])
const categories = ref<Row[]>([])
const flags = ref<Row[]>([])
const category = ref('')
const keyword = ref('')
const page = ref(1)
const total = ref(0)
const modal = ref('')
const form = ref<Record<string, string>>({})
const overview = ref<{ onlineBots: string[]; categories: number; kfc: number; subscriptions: number; logs: number }>()
const columns = computed(() => {
  if (section === 'dictionary') return [{ key: 'dictCode', name: '字典编码' }, { key: 'dictName', name: '字典名称' }, { key: 'modifyTime', name: '更新时间' }]
  if (section === 'kfc') return [{ key: 'dataId', name: '编号' }, { key: 'kfcText', name: '文案内容' }, { key: 'modifyTime', name: '更新时间' }]
  if (section === 'subscriptions') return [{ key: 'eventFlag', name: '订阅事件' }, { key: 'permitType', name: '目标类型' }, { key: 'target', name: '群号 / QQ 号' }, { key: 'createTime', name: '创建时间' }]
  return [{ key: 'methodId', name: '监听方法' }, { key: 'userId', name: '用户 QQ' }, { key: 'groupId', name: '群号' }, { key: 'command', name: '触发消息' }, { key: 'invokeTime', name: '调用时间' }]
})
const stats = computed(() => [
  { label: '在线机器人', value: overview.value?.onlineBots.length ?? 0, detail: '当前连接', link: '/overview' },
  { label: '数据字典', value: overview.value?.categories ?? 0, detail: '分类', link: '/dictionary' },
  { label: 'KFC 文案', value: overview.value?.kfc ?? 0, detail: '条内容', link: '/kfc' },
  { label: '有效订阅', value: overview.value?.subscriptions ?? 0, detail: '项订阅', link: '/subscriptions' },
])
function format(value: Row[string] | undefined, key: string) {
  if (key === 'permitType') return Number(value) === 1 ? '群聊' : '私聊'
  if (value === null || value === undefined || value === '') return '—'
  if (key.endsWith('Time')) { const date = new Date(String(value)); return Number.isNaN(date.getTime()) ? String(value) : date.toLocaleString('zh-CN', { hour12: false }) }
  return String(value)
}
async function load() {
  loading.value = true; error.value = ''
  try {
    if (section === 'overview') {
      const [summary, recent] = await Promise.all([request<NonNullable<typeof overview.value>>('/api/admin/overview'), request<PageData>('/api/admin/logs')])
      overview.value = summary; rows.value = recent.records; total.value = recent.total
    } else if (section === 'dictionary') {
      categories.value = await request<Row[]>('/api/admin/categories')
      if (!categories.value.some(item => item.catCode === category.value)) category.value = String(categories.value[0]?.catCode ?? '')
      rows.value = category.value ? await request<Row[]>('/api/admin/details?catCode=' + encodeURIComponent(category.value)) : []
      total.value = rows.value.length
    } else {
      if (section === 'subscriptions') flags.value = await request<Row[]>('/api/admin/details?catCode=EVENT_FLAG')
      const result = await request<PageData>('/api/admin/' + section + '?' + new URLSearchParams({ page: String(page.value), keyword: keyword.value }))
      rows.value = result.records; total.value = result.total
      if (!rows.value.length && page.value > 1) { page.value--; await load() }
    }
  } catch (e) { error.value = (e as Error).message }
  finally { loading.value = false }
}
function open(kind: string, row?: Row) {
  error.value = ''; success.value = ''
  form.value = row ? Object.fromEntries(Object.entries(row).map(([key, value]) => [key, value == null ? '' : String(value)])) : { permitType: '1', eventFlag: String(flags.value[0]?.dictCode ?? '') }
  modal.value = kind
}
async function save() {
  saving.value = true; error.value = ''
  try {
    let path = section
    let payload: unknown = form.value
    if (modal.value === 'category') path = 'categories'
    else if (section === 'dictionary') { path = 'details'; payload = { ...form.value, catCode: category.value } }
    else if (section === 'kfc') payload = { dataId: form.value.dataId ? Number(form.value.dataId) : null, kfcText: form.value.kfcText }
    else if (section === 'subscriptions') payload = { ...form.value, permitType: Number(form.value.permitType) }
    await request('/api/admin/' + path, 'POST', payload)
    if (modal.value === 'category') category.value = form.value.catCode ?? ''
    modal.value = ''; success.value = '保存成功'; await load()
  } catch (e) { error.value = (e as Error).message }
  finally { saving.value = false }
}
async function remove(row: Row) {
  if (!window.confirm(section === 'kfc' ? '确认删除这条文案？' : '确认取消这项订阅？')) return
  saving.value = true
  try {
    await request('/api/admin/' + section + '/' + (row.dataId ?? row.permitId), 'DELETE')
    success.value = section === 'kfc' ? '文案已删除' : '订阅已取消'; await load()
  } catch (e) { error.value = (e as Error).message }
  finally { saving.value = false }
}
function search() { page.value = 1; void load() }
function changePage(delta: number) { page.value += delta; void load() }
onMounted(load)
</script>

<template>
  <main class="page">
    <div class="page-heading"><div><p class="eyebrow">CHOWH BOT</p><h1>{{ title }}</h1><p class="muted">{{ description }}</p></div><button class="secondary" :disabled="loading" @click="load">↻ 刷新数据</button></div>
    <p v-if="error && !modal" role="alert" class="error">{{ error }}</p>
    <p v-if="success" role="status" class="success">{{ success }}</p>
    <template v-if="section === 'overview'">
      <div class="stats-grid"><RouterLink v-for="(stat, index) in stats" :key="stat.label" :to="stat.link" class="stat-card"><div class="stat-top"><span>{{ stat.label }}</span><span class="stat-icon">{{ ['◉', '▤', '▧', '↗'][index] }}</span></div><strong>{{ loading && !overview ? '—' : stat.value }}</strong><small>{{ stat.detail }}</small></RouterLink></div>
      <section class="connection-card"><div class="connection-symbol">◉</div><div><h2>机器人连接</h2><p class="muted">{{ overview?.onlineBots.length ? overview.onlineBots.join(' · ') : '当前没有已连接的机器人' }}</p></div><span class="status-pill" :class="{ online: overview?.onlineBots.length }">{{ overview?.onlineBots.length ? '已连接' : '离线' }}</span></section>
    </template>
    <section class="data-card">
      <div class="card-heading"><div><h2>{{ section === 'overview' ? '最近调用' : title }}<span class="count-badge">{{ total }}</span></h2><p v-if="section === 'overview'" class="muted">按调用时间查看最近的监听事件</p></div><RouterLink v-if="section === 'overview'" to="/logs" class="text-link">查看全部 →</RouterLink></div>
      <div v-if="section !== 'overview'" class="toolbar">
        <template v-if="section === 'dictionary'"><select v-model="category" aria-label="字典分类" @change="load"><option v-if="!categories.length" value="">暂无字典分类</option><option v-for="cat in categories" :key="String(cat.catCode)" :value="cat.catCode">{{ cat.catName }} · {{ cat.catCode }}</option></select><button class="secondary" @click="open('category')">新增分类</button><button class="secondary" :disabled="!category" @click="open('category', categories.find(item => item.catCode === category))">编辑分类</button><button class="primary push-right" :disabled="!category" @click="open('edit')">＋ 新增明细</button></template>
        <template v-else><form class="search" @submit.prevent="search"><input v-model="keyword" :placeholder="section === 'logs' ? '搜索监听方法、QQ、群号或消息' : section === 'kfc' ? '搜索文案内容' : '搜索订阅编码'" aria-label="搜索"><button class="secondary" :disabled="loading">搜索</button></form><button v-if="section !== 'logs'" class="primary push-right" @click="open('edit')">{{ section === 'kfc' ? '＋ 新增文案' : '＋ 新增订阅' }}</button></template>
      </div>
      <div class="table-scroll"><table><thead><tr><th v-for="col in columns" :key="col.key">{{ col.name }}</th><th v-if="['dictionary', 'kfc', 'subscriptions'].includes(section)" class="actions">操作</th></tr></thead><tbody>
        <tr v-if="loading"><td :colspan="columns.length + 1" class="empty">正在加载数据…</td></tr>
        <tr v-else-if="!rows.length"><td :colspan="columns.length + 1" class="empty"><div class="empty-icon">▤</div>暂无数据</td></tr>
        <template v-else><tr v-for="(row, index) in rows" :key="String(row.logId ?? row.dataId ?? row.permitId ?? row.dictCode ?? index)">
          <td v-for="col in columns" :key="col.key" :class="{ mono: ['methodId', 'dictCode', 'target', 'userId', 'groupId'].includes(col.key), 'message-cell': ['kfcText', 'command'].includes(col.key) }"><span v-if="col.key === 'permitType'" class="type-pill">{{ format(row[col.key], col.key) }}</span><template v-else>{{ format(row[col.key], col.key) }}</template></td>
          <td v-if="['dictionary', 'kfc', 'subscriptions'].includes(section)" class="actions"><button v-if="section !== 'subscriptions'" class="text-button" :disabled="saving" @click="open('edit', row)">编辑</button><button v-if="section !== 'dictionary'" class="text-button danger" :disabled="saving" @click="remove(row)">{{ section === 'kfc' ? '删除' : '取消订阅' }}</button></td>
        </tr></template>
      </tbody></table></div>
      <div class="table-footer"><span>{{ section === 'overview' ? '最近 15 条' : '共 ' + total + ' 条记录' }}</span><div v-if="!['overview', 'dictionary'].includes(section)" class="pagination"><button class="secondary" :disabled="page === 1 || loading" @click="changePage(-1)">上一页</button><span>{{ page }} / {{ Math.max(1, Math.ceil(total / 15)) }}</span><button class="secondary" :disabled="page * 15 >= total || loading" @click="changePage(1)">下一页</button></div></div>
    </section>
    <p class="page-footer">Chowh Bot · 管理控制台</p>
    <div v-if="modal" class="modal-backdrop" @click.self="!saving && (modal = '')">
      <section class="modal" role="dialog" aria-modal="true" aria-labelledby="dialog-title">
        <div class="modal-heading"><h2 id="dialog-title">{{ modal === 'category' ? '字典分类' : section === 'kfc' ? '编辑 KFC 文案' : section === 'dictionary' ? '字典明细' : '新增订阅' }}</h2><button aria-label="关闭" :disabled="saving" @click="modal = ''">×</button></div>
        <form @submit.prevent="save">
          <template v-if="modal === 'category'"><label>分类编码<input v-model="form.catCode" required :readonly="!!form.createTime" maxlength="100"></label><label>分类名称<input v-model="form.catName" required maxlength="200"></label></template>
          <template v-else-if="section === 'dictionary'"><label>字典编码<input v-model="form.dictCode" required :readonly="!!form.detailId" maxlength="100"></label><label>字典名称<input v-model="form.dictName" required maxlength="500"></label></template>
          <template v-else-if="section === 'kfc'"><label>文案内容<textarea v-model="form.kfcText" rows="10" required placeholder="支持文字和 [CQ:image,file=#{BOT_MINIO_ENDPOINT}/…]"></textarea></label></template>
          <template v-else><label>订阅事件<select v-model="form.eventFlag" required><option value="" disabled>请选择事件</option><option v-for="flag in flags" :key="String(flag.dictCode)" :value="flag.dictCode">{{ flag.dictName }} · {{ flag.dictCode }}</option></select></label><label>目标类型<select v-model="form.permitType"><option value="1">群聊</option><option value="0">私聊</option></select></label><label>{{ form.permitType === '1' ? '群号' : 'QQ 号' }}<input v-model="form.target" inputmode="numeric" pattern="[0-9]+" required></label></template>
          <p v-if="error" class="error" role="alert">{{ error }}</p>
          <div class="modal-actions"><button type="button" class="secondary" :disabled="saving" @click="modal = ''">取消</button><button class="primary" :disabled="saving">{{ saving ? '保存中…' : '保存' }}</button></div>
        </form>
      </section>
    </div>
  </main>
</template>
