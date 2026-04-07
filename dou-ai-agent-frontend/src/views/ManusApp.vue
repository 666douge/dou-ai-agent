<template>
  <div class="chat-page">
    <!-- 顶部导航栏 -->
    <header class="chat-nav">
      <button class="nav-back" @click="goBack" title="返回">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
          <polyline points="15 18 9 12 15 6"/>
        </svg>
      </button>
      <div class="nav-info">
        <h1 class="nav-title">AI 超级智能体</h1>
        <span class="nav-sub">会话ID: {{ chatId }}</span>
      </div>
      <div class="nav-spacer"></div>
    </header>

    <!-- 消息列表 -->
    <main class="chat-feed" ref="feedRef">
      <!-- 空状态 -->
      <div v-if="messages.length === 0" class="feed-empty">
        <div class="empty-icon-wrap">
          <svg width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
            <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/>
          </svg>
        </div>
        <p class="empty-title">欢迎来到 AI 超级智能体</p>
        <p class="empty-hint">我可以帮您完成各种复杂任务，请告诉我您的需求</p>
      </div>

      <!-- 消息气泡（每条 SSE data 单独一条气泡） -->
      <ChatBubble
        v-for="(msg, idx) in messages"
        :key="idx"
        :content="msg.content"
        :isUser="msg.isUser"
        :time="msg.time"
        appType="manus"
      />

      <!-- AI 正在输入指示器 -->
      <div v-if="aiTyping" class="feed-typing">
        <div class="typing-avatar">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/>
          </svg>
        </div>
        <div class="typing-dots">
          <span></span><span></span><span></span>
        </div>
      </div>
    </main>

    <!-- 输入区 -->
    <ChatInput
      :disabled="aiTyping"
      placeholder="描述您的任务需求..."
      @send="handleSend"
    />
  </div>
</template>

<script setup>
import { ref, nextTick, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import ChatBubble from '@/components/ChatBubble.vue'
import ChatInput from '@/components/ChatInput.vue'
import { streamChatManus } from '@/api/ai'

const router = useRouter()

const chatId  = ref(generateId())
const messages = ref([])
const aiTyping = ref(false)
const feedRef = ref(null)

function generateId() {
  return 'chat_' + Date.now() + '_' + Math.random().toString(36).slice(2, 9)
}

function goBack() { router.push('/') }

function fmtTime() {
  const d = new Date()
  return `${d.getHours().toString().padStart(2,'0')}:${d.getMinutes().toString().padStart(2,'0')}`
}

function scrollBottom() {
  nextTick(() => {
    if (feedRef.value) {
      feedRef.value.scrollTop = feedRef.value.scrollHeight
    }
  })
}

function handleSend(text) {
  messages.value.push({ content: text, isUser: true, time: fmtTime() })
  scrollBottom()
  aiTyping.value = true

  // 每条 SSE data 对应一个步骤，独立一条气泡（与恋爱大师拼接模式不同）
  streamChatManus(text, {
    onMessage(data) {
      const chunk = typeof data === 'string' ? data : String(data)
      if (!chunk.trim()) return
      messages.value.push({ content: chunk, isUser: false, time: fmtTime() })
      scrollBottom()
    },
    onError(err) {
      console.error(err)
      aiTyping.value = false
      messages.value.push({ content: '抱歉发生了错误，请稍后重试。', isUser: false, time: fmtTime() })
      scrollBottom()
    },
    onDone() { aiTyping.value = false }
  })
}

onMounted(scrollBottom)
</script>

<style scoped>
/* ── 页面 ── */
.chat-page {
  display: flex;
  flex-direction: column;
  height: 100dvh;
  background: var(--color-bg);
}

/* ── 导航栏 ── */
.chat-nav {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  background: var(--color-surface);
  border-bottom: 1px solid var(--color-border);
  position: sticky;
  top: 0;
  z-index: 10;
  flex-shrink: 0;
}

.nav-back {
  width: 38px;
  height: 38px;
  border-radius: var(--radius-full);
  border: none;
  background: var(--color-bg);
  color: var(--color-text-secondary);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  transition: all var(--transition-fast);
}

.nav-back:hover {
  background: var(--color-border);
  color: var(--color-text-primary);
}

.nav-info {
  display: flex;
  flex-direction: column;
  gap: 1px;
}

.nav-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--color-text-primary);
  margin: 0;
}

.nav-sub {
  font-size: 11px;
  color: var(--color-text-muted);
}

.nav-spacer {
  flex: 1;
}

/* ── 消息流 ── */
.chat-feed {
  flex: 1;
  overflow-y: auto;
  padding: 20px 16px;
  display: flex;
  flex-direction: column;
  gap: 0;
}

/* ── 空状态 ── */
.feed-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  flex: 1;
  min-height: 300px;
  text-align: center;
  padding: 40px 20px;
  gap: 10px;
}

.empty-icon-wrap {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.12) 0%, rgba(99, 102, 241, 0.08) 100%);
  color: #818cf8;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 6px;
  box-shadow: 0 8px 32px rgba(59, 130, 246, 0.15);
}

.empty-title {
  font-size: 17px;
  font-weight: 600;
  color: var(--color-text-primary);
  margin: 0;
}

.empty-hint {
  font-size: 14px;
  color: var(--color-text-muted);
  margin: 0;
  max-width: 280px;
  line-height: 1.6;
}

/* ── 输入中指示器 ── */
.feed-typing {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-top: 8px;
  padding-left: 2px;
}

.typing-avatar {
  width: var(--avatar-size);
  height: var(--avatar-size);
  border-radius: var(--radius-full);
  background: linear-gradient(135deg, #3b82f6 0%, #6366f1 100%);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 4px 14px rgba(59, 130, 246, 0.3);
}

.typing-dots {
  display: flex;
  align-items: center;
  gap: 5px;
  padding: 11px 16px;
  background: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  border-bottom-left-radius: 6px;
}

.typing-dots span {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: var(--color-text-muted);
  animation: dotBounce 1.4s infinite ease-in-out;
}

.typing-dots span:nth-child(1) { animation-delay: 0s; }
.typing-dots span:nth-child(2) { animation-delay: 0.2s; }
.typing-dots span:nth-child(3) { animation-delay: 0.4s; }

@keyframes dotBounce {
  0%, 80%, 100% { transform: translateY(0);    opacity: 0.5; }
  40%            { transform: translateY(-7px); opacity: 1;   }
}

/* ── 响应式 ── */
@media (max-width: 480px) {
  .chat-nav {
    padding: 10px 12px;
  }

  .chat-feed {
    padding: 14px 10px;
  }

  .feed-empty {
    min-height: 240px;
    padding: 32px 16px;
  }
}
</style>
