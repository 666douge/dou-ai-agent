<template>
  <div class="chat-page">
    <div class="chat-header">
      <button class="back-btn" @click="goBack">
        <span>←</span>
      </button>
      <div class="header-info">
        <h1 class="header-title">AI 超级智能体</h1>
        <span class="chat-id">会话ID: {{ chatId }}</span>
      </div>
    </div>

    <div class="chat-messages" ref="messagesContainer">
      <div v-if="messages.length === 0" class="empty-state">
        <div class="empty-icon">🤖</div>
        <p>欢迎来到 AI 超级智能体</p>
        <p class="empty-hint">我可以帮您完成各种复杂任务，请告诉我您的需求</p>
      </div>

      <ChatBubble
        v-for="(msg, index) in messages"
        :key="index"
        :content="msg.content"
        :isUser="msg.isUser"
        :time="msg.time"
      />

      <div v-if="aiTyping" class="ai-typing">
        <div class="typing-indicator">
          <span></span>
          <span></span>
          <span></span>
        </div>
      </div>
    </div>

    <ChatInput
      :disabled="aiTyping"
      placeholder="输入您想说的话..."
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

const chatId = ref(generateChatId())
const messages = ref([])
const aiTyping = ref(false)
const messagesContainer = ref(null)

function generateChatId() {
  return 'chat_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9)
}

function goBack() {
  router.push('/')
}

function formatTime() {
  const now = new Date()
  return `${now.getHours().toString().padStart(2, '0')}:${now.getMinutes().toString().padStart(2, '0')}`
}

function scrollToBottom() {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  })
}

function handleSend(text) {
  messages.value.push({
    content: text,
    isUser: true,
    time: formatTime()
  })
  scrollToBottom()

  aiTyping.value = true

  // 后端每条 SSE data 对应智能体一个步骤，单独一条气泡展示（与恋爱大师页拼接模式不同）
  streamChatManus(text, {
    onMessage: (data) => {
      const chunk = typeof data === 'string' ? data : String(data)
      if (!chunk.trim()) return
      messages.value.push({
        content: chunk,
        isUser: false,
        time: formatTime()
      })
      scrollToBottom()
    },
    onError: (error) => {
      console.error('SSE 错误:', error)
      aiTyping.value = false
      messages.value.push({
        content: '抱歉，发生了错误，请稍后重试。',
        isUser: false,
        time: formatTime()
      })
      scrollToBottom()
    },
    onDone: () => {
      aiTyping.value = false
    }
  })
}

onMounted(() => {
  scrollToBottom()
})
</script>

<style scoped>
.chat-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background: #f8f9fa;
  font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

.chat-header {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  background: #fff;
  border-bottom: 1px solid #eee;
  position: sticky;
  top: 0;
  z-index: 10;
}

.back-btn {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  border: none;
  background: #f5f5f5;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  color: #333;
  transition: all 0.2s;
  margin-right: 12px;
}

.back-btn:hover {
  background: #e8e8e8;
}

.header-info {
  flex: 1;
}

.header-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.chat-id {
  font-size: 11px;
  color: #999;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  scroll-behavior: smooth;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #666;
}

.empty-icon {
  font-size: 60px;
  margin-bottom: 16px;
}

.empty-state p {
  margin: 0 0 8px;
  font-size: 16px;
}

.empty-hint {
  font-size: 13px !important;
  color: #999 !important;
}

.ai-typing {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
  padding-left: 50px;
}

.typing-indicator {
  display: flex;
  gap: 4px;
  padding: 12px 16px;
  background: #f1f0f0;
  border-radius: 18px;
  border-bottom-left-radius: 4px;
}

.typing-indicator span {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #999;
  animation: typing 1.4s infinite;
}

.typing-indicator span:nth-child(2) {
  animation-delay: 0.2s;
}

.typing-indicator span:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes typing {
  0%, 60%, 100% {
    transform: translateY(0);
    opacity: 0.6;
  }
  30% {
    transform: translateY(-8px);
    opacity: 1;
  }
}
</style>