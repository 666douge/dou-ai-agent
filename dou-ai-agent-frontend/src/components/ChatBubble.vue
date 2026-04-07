<template>
  <div class="chat-bubble" :class="{ 'user': isUser, 'ai': !isUser }">
    <div class="avatar">
      <div v-if="!isUser" class="ai-avatar">🤖</div>
      <div v-else class="user-avatar">👤</div>
    </div>
    <div class="message-content">
      <div class="message-text">{{ content }}</div>
      <div class="message-time">{{ time }}</div>
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
  }
})
</script>

<style scoped>
.chat-bubble {
  display: flex;
  margin-bottom: 16px;
  max-width: 100%;
}

.chat-bubble.user {
  flex-direction: row-reverse;
}

.avatar {
  flex-shrink: 0;
  width: 40px;
  height: 40px;
  margin: 0 10px;
}

.avatar img {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
}

.ai-avatar {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
}

.user-avatar {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
}

.message-content {
  max-width: 70%;
  min-width: 60px;
}

.chat-bubble.user .message-content {
  text-align: right;
}

.message-text {
  padding: 12px 16px;
  border-radius: 18px;
  line-height: 1.5;
  word-break: break-word;
  text-align: left;
}

.chat-bubble.ai .message-text {
  background: #f1f0f0;
  color: #333;
  border-bottom-left-radius: 4px;
}

.chat-bubble.user .message-text {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  border-bottom-right-radius: 4px;
}

.message-time {
  font-size: 11px;
  color: #999;
  margin-top: 4px;
  padding: 0 4px;
}
</style>