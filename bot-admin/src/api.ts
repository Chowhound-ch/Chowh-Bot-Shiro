export type Row = Record<string, string | number | boolean | null>
export interface PageData { records: Row[]; total: number }
export interface Session { userId: string; token?: string; expiresAt: number }
const TOKEN_KEY = 'chowh-admin-token'
let token = sessionStorage.getItem(TOKEN_KEY) ?? ''

export function setSession(session?: Session) {
  if (!session) {
    token = ''
    sessionStorage.removeItem(TOKEN_KEY)
  } else if (session.token) {
    token = session.token
    sessionStorage.setItem(TOKEN_KEY, token)
  }
}

export async function request<T>(path: string, method = 'GET', body?: unknown): Promise<T> {
  const response = await fetch(path, {
    method,
    credentials: 'omit',
    headers: {
      'Content-Type': 'application/json',
      ...(token ? { Authorization: 'Bearer ' + token } : {}),
    },
    body: body === undefined ? undefined : JSON.stringify(body),
  })
  if (response.status === 401) {
    setSession()
    window.dispatchEvent(new Event('session-expired'))
    throw new Error('登录已过期，请重新登录')
  }
  if (response.status === 403) {
    setSession()
    window.dispatchEvent(new Event('session-expired'))
    throw new Error('需要管理员权限，请重新登录')
  }
  if (!response.ok) throw new Error(response.status === 503 ? '数据服务暂不可用，请检查 Redis 和数据库连接' : '请求失败，请检查后端服务')
  const result = await response.json()
  if (result.code !== '0') throw new Error(result.msg || '操作失败')
  return result.data as T
}
