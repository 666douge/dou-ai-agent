/**
 * 后端 API 根路径
 *
 * 读取规则（优先级从高到低）：
 *   1. 绝对 URL 形式（如 https://backend.com）→ 直接使用，不走代理
 *   2. 相对路径形式（如 /api）→ 开发时走 Vite proxy，生产时使用相对路径
 *
 * .env 文件配置示例：
 *   开发：VITE_API_BASE_URL=/api          （vite.config.js proxy 转发到 localhost:8123）
 *   生产：VITE_API_BASE_URL=https://your-backend.com
 */
const BASE_URL = (() => {
  const val = import.meta.env.VITE_API_BASE_URL
  // 相对路径（开发）→ 保持 /api；绝对 URL（生产）→ 直接使用
  if (val && (val.startsWith('http://') || val.startsWith('https://'))) {
    return val.replace(/\/$/, '') // 去掉末尾斜杠
  }
  return val || '/api'
})()

/* ══════════════════════════════════════════════
   SSE 工具函数
   ══════════════════════════════════════════════ */

/**
 * 解析 SSE data: 行并回调
 * @param {string}   text      原始文本块
 * @param {function}  onMessage 回调
 */
function emitDataLines(text, onMessage) {
  const lines = text.split('\n')
  for (const line of lines) {
    const trimmed = line.trim()
    if (trimmed.startsWith('data:')) {
      const data = trimmed.slice(5).trim()
      if (data) onMessage(data)
    }
  }
}

/**
 * 确保 onDone 只执行一次的守卫
 */
function createSettler(onDone) {
  let settled = false
  return () => {
    if (settled) return
    settled = true
    onDone?.()
  }
}

/* ══════════════════════════════════════════════
   AI 恋爱大师 — 流式聊天
   ══════════════════════════════════════════════ */

/**
 * 通过 SSE 方式调用 AI 恋爱大师接口（Spring WebFlux Flux<String>）
 *
 * @param {string}   message    用户消息
 * @param {string}   chatId     会话 ID
 * @param {Object}   callbacks
 * @param {function}  callbacks.onMessage  收到片段回调
 * @param {function}  callbacks.onError   错误回调
 * @param {function}  callbacks.onDone    完成回调
 * @returns {AbortController}
 */
export function streamChatSSE(message, chatId, { onMessage, onError, onDone }) {
  const controller = new AbortController()
  const settle = createSettler(onDone)

  const encodedMessage = encodeURIComponent(message)
  const encodedChatId = encodeURIComponent(chatId)
  const url = `${BASE_URL}/ai/love_app/chat/sse?message=${encodedMessage}&chatId=${encodedChatId}`

  fetch(url, {
    method: 'GET',
    headers: { 'Accept': 'text/event-stream' },
    signal: controller.signal,
  })
    .then(response => {
      if (!response.ok) throw new Error(`HTTP ${response.status}`)
      const reader = response.body.getReader()
      const decoder = new TextDecoder()
      let buffer = ''

      function read() {
        reader.read().then(({ done, value }) => {
          if (done) {
            if (buffer.trim()) emitDataLines(buffer, onMessage)
            settle()
            return
          }
          buffer += decoder.decode(value, { stream: true })
          const parts = buffer.split('\n')
          buffer = parts.pop() || ''
          for (const part of parts) emitDataLines(part, onMessage)
          read()
        }).catch(err => {
          if (err.name !== 'AbortError') onError?.(err)
          settle()
        })
      }

      read()
    })
    .catch(err => {
      if (err.name !== 'AbortError') onError?.(err)
      settle()
    })

  return controller
}

/* ══════════════════════════════════════════════
   AI 超级智能体 — 流式聊天
   ══════════════════════════════════════════════ */

/**
 * 通过 SSE 方式调用 AI 超级智能体接口（Spring SseEmitter）
 *
 * @param {string}   message    用户消息
 * @param {Object}   callbacks
 * @param {function}  callbacks.onMessage  收到片段回调
 * @param {function}  callbacks.onError   错误回调
 * @param {function}  callbacks.onDone    完成回调
 * @returns {AbortController}
 */
export function streamChatManus(message, { onMessage, onError, onDone }) {
  const controller = new AbortController()
  const settle = createSettler(onDone)

  const encodedMessage = encodeURIComponent(message)
  const url = `${BASE_URL}/ai/love_app/chat/manus?message=${encodedMessage}`

  fetch(url, {
    method: 'GET',
    headers: { 'Accept': 'text/event-stream' },
    signal: controller.signal,
  })
    .then(response => {
      if (!response.ok) throw new Error(`HTTP ${response.status}`)
      const reader = response.body.getReader()
      const decoder = new TextDecoder()
      let buffer = ''

      function read() {
        reader.read().then(({ done, value }) => {
          if (done) {
            if (buffer.trim()) emitDataLines(buffer, onMessage)
            settle()
            return
          }
          buffer += decoder.decode(value, { stream: true })
          const parts = buffer.split('\n')
          buffer = parts.pop() || ''
          for (const part of parts) emitDataLines(part, onMessage)
          read()
        }).catch(err => {
          if (err.name !== 'AbortError') onError?.(err)
          settle()
          settle() // settle() 内部有幂等保护，重复调用无影响
        })
      }

      read()
    })
    .catch(err => {
      if (err.name !== 'AbortError') onError?.(err)
      settle()
    })

  return controller
}
