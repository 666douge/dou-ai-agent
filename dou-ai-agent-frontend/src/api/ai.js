const BASE_URL = '/api'

/**
 * 通过 SSE 方式调用 AI 聊天接口（Spring WebFlux Flux<String>）
 * @param {string} message - 用户消息
 * @param {string} chatId - 聊天室 ID
 * @param {function} onMessage - 收到消息片段的回调
 * @param {function} onError - 错误回调
 * @param {function} onDone - 完成回调
 * @returns {AbortController}
 */
export function streamChatSSE(message, chatId, { onMessage, onError, onDone }) {
  const controller = new AbortController()

  const encodedMessage = encodeURIComponent(message)
  const encodedChatId = encodeURIComponent(chatId)
  const url = `${BASE_URL}/ai/love_app/chat/sse?message=${encodedMessage}&chatId=${encodedChatId}`

  fetch(url, {
    method: 'GET',
    headers: { 'Accept': 'text/event-stream' },
    signal: controller.signal
  })
    .then(response => {
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}`)
      }
      const reader = response.body.getReader()
      const decoder = new TextDecoder()
      let buffer = ''

      function read() {
        reader.read().then(({ done, value }) => {
          if (done) {
            if (buffer.trim()) {
              onMessage(buffer)
            }
            onDone && onDone()
            return
          }
          buffer += decoder.decode(value, { stream: true })
          const lines = buffer.split('\n')
          buffer = lines.pop() || ''

          for (const line of lines) {
            const trimmed = line.trim()
            if (trimmed.startsWith('data:')) {
              const data = trimmed.slice(5).trim()
              if (data) {
                onMessage(data)
              }
            }
          }
          read()
        }).catch(err => {
          if (err.name !== 'AbortError') {
            onError && onError(err)
          }
        })
      }

      read()
    })
    .catch(err => {
      if (err.name !== 'AbortError') {
        onError && onError(err)
      }
    })

  return controller
}

/**
 * 通过 SSE 方式调用 Manus 智能体接口（Spring SseEmitter）
 * @param {string} message - 用户消息
 * @param {function} onMessage - 收到消息片段的回调
 * @param {function} onError - 错误回调
 * @param {function} onDone - 完成回调
 * @returns {AbortController}
 */
export function streamChatManus(message, { onMessage, onError, onDone }) {
  const controller = new AbortController()

  const encodedMessage = encodeURIComponent(message)
  const url = `${BASE_URL}/ai/love_app/chat/manus?message=${encodedMessage}`

  fetch(url, {
    method: 'GET',
    headers: { 'Accept': 'text/event-stream' },
    signal: controller.signal
  })
    .then(response => {
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}`)
      }
      const reader = response.body.getReader()
      const decoder = new TextDecoder()
      let buffer = ''

      function read() {
        reader.read().then(({ done, value }) => {
          if (done) {
            if (buffer.trim()) {
              onMessage(buffer)
            }
            onDone && onDone()
            return
          }
          buffer += decoder.decode(value, { stream: true })
          const lines = buffer.split('\n')
          buffer = lines.pop() || ''

          for (const line of lines) {
            const trimmed = line.trim()
            if (trimmed.startsWith('data:')) {
              const data = trimmed.slice(5).trim()
              if (data) {
                onMessage(data)
              }
            }
          }
          read()
        }).catch(err => {
          if (err.name !== 'AbortError') {
            onError && onError(err)
          }
        })
      }

      read()
    })
    .catch(err => {
      if (err.name !== 'AbortError') {
        onError && onError(err)
      }
    })

  return controller
}