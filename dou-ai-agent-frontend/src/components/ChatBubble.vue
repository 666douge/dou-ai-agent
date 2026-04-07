<template>
  <div class="chat-bubble" :class="{ 'is-user': isUser }">
    <!-- 头像 -->
    <div class="bubble-avatar">
      <!-- 用户头像 -->
      <div v-if="isUser" class="avatar-circle user-avatar">
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
          <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
          <circle cx="12" cy="7" r="4"/>
        </svg>
      </div>
      <!-- AI 头像 — 按应用类型区分 -->
      <div v-else class="avatar-circle ai-avatar" :class="`ai-${appType}`">
        <!-- 恋爱大师 -->
        <template v-if="appType === 'love'">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="currentColor">
            <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"/>
          </svg>
        </template>
        <!-- 超级智能体 -->
        <template v-else-if="appType === 'manus'">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/>
          </svg>
        </template>
        <!-- 默认兜底 -->
        <template v-else>
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
            <circle cx="12" cy="12" r="10"/>
            <path d="M8 14s1.5 2 4 2 4-2 4-2"/>
            <line x1="9" y1="9" x2="9.01" y2="9"/>
            <line x1="15" y1="9" x2="15.01" y2="9"/>
          </svg>
        </template>
      </div>
    </div>

    <!-- 消息内容 -->
    <div class="bubble-body">
      <div class="bubble-text">{{ content }}</div>
      <div class="bubble-time">{{ time }}</div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  content: {
    type: String,
    required: true
  },
  isUser: {
    type: Boolean,
    default: false
  },
  time: {
    type: String,
    default: ''
  },
  appType: {
    type: String,
    default: 'default'
  }
})
</script>

<style scoped>
/* ── 气泡容器 ── */
.chat-bubble {
  display: flex;
  align-items: flex-end;
  gap: 10px;
  margin-bottom: 14px;
  animation: bubbleIn 0.25s cubic-bezier(0.34, 1.56, 0.64, 1) both;
}

.chat-bubble.is-user {
  flex-direction: row-reverse;
}

/* ── 头像 ── */
.bubble-avatar {
  flex-shrink: 0;
}

.avatar-circle {
  width: var(--avatar-size);
  height: var(--avatar-size);
  border-radius: var(--radius-full);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: var(--shadow-sm);
  color: #fff;
  transition: transform var(--transition-fast);
}

.avatar-circle:hover {
  transform: scale(1.06);
}

/* 用户头像 */
.user-avatar {
  background: var(--color-primary);
  color: #fff;
}

/* 恋爱大师 AI 头像 — 粉色渐变 */
.ai-love {
  background: linear-gradient(135deg, #ec4899 0%, #f43f5e 50%, #fb7185 100%);
  box-shadow: 0 4px 14px rgba(236, 72, 153, 0.35);
}

/* 超级智能体 AI 头像 — 科技蓝 */
.ai-manus {
  background: linear-gradient(135deg, #3b82f6 0%, #6366f1 50%, #8b5cf6 100%);
  box-shadow: 0 4px 14px rgba(59, 130, 246, 0.35);
}

/* 默认 AI 头像 */
.avatar-circle:not(.user-avatar):not(.ai-love):not(.ai-manus) {
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-secondary) 100%);
  box-shadow: var(--shadow-md);
}

/* ── 气泡主体 ── */
.bubble-body {
  max-width: var(--bubble-max-width);
  min-width: 64px;
}

.chat-bubble.is-user .bubble-body {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.bubble-text {
  padding: var(--bubble-padding);
  border-radius: var(--radius-lg);
  line-height: 1.6;
  word-break: break-word;
  white-space: pre-wrap;
  font-size: 15px;
  text-align: left;
  box-shadow: var(--shadow-sm);
}

/* AI 气泡 — 白色，左下圆角稍小 */
.chat-bubble:not(.is-user) .bubble-text {
  background: var(--color-bubble-ai);
  color: var(--color-text-primary);
  border-bottom-left-radius: 6px;
  border: 1px solid var(--color-border);
}

/* 用户气泡 — 渐变色，右下圆角稍小 */
.chat-bubble.is-user .bubble-text {
  background: var(--color-bubble-user);
  color: #fff;
  border-bottom-right-radius: 6px;
}

/* 时间戳 */
.bubble-time {
  font-size: 11px;
  color: var(--color-text-muted);
  margin-top: 4px;
  padding: 0 4px;
}

/* ── 气泡入场动画 ── */
@keyframes bubbleIn {
  from {
    opacity: 0;
    transform: translateY(8px) scale(0.96);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

/* ── 响应式微调 ── */
@media (max-width: 480px) {
  .chat-bubble {
    gap: 8px;
    margin-bottom: 10px;
  }

  .bubble-text {
    font-size: 14px;
  }

  .bubble-time {
    font-size: 10px;
  }
}
</style>
