<template>
  <div class="chat-input-wrapper">
    <div class="chat-input-container">
      <textarea
        ref="textareaRef"
        v-model="inputText"
        class="chat-input"
        :placeholder="placeholder"
        :disabled="disabled"
        @keydown.enter.exact.prevent="handleSend"
        @input="autoResize"
      ></textarea>
      <button
        class="send-btn"
        :class="{ 'disabled': disabled || !inputText.trim() }"
        :disabled="disabled || !inputText.trim()"
        @click="handleSend"
      >
        <span class="send-icon">↑</span>
      </button>
    </div>
    <div class="input-hint">按 Enter 发送，Shift + Enter 换行</div>
  </div>
</template>

<script setup>
import { ref, nextTick } from 'vue'

const props = defineProps({
  placeholder: {
    type: String,
    default: '输入消息...'
  },
  disabled: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['send'])

const inputText = ref('')
const textareaRef = ref(null)

function handleSend() {
  const text = inputText.value.trim()
  if (!text || props.disabled) return

  emit('send', text)
  inputText.value = ''

  nextTick(() => {
    if (textareaRef.value) {
      textareaRef.value.style.height = 'auto'
    }
  })
}

function autoResize() {
  const textarea = textareaRef.value
  if (!textarea) return
  textarea.style.height = 'auto'
  const maxHeight = 120
  textarea.style.height = Math.min(textarea.scrollHeight, maxHeight) + 'px'
}
</script>

<style scoped>
.chat-input-wrapper {
  padding: 12px 16px;
  background: #fff;
  border-top: 1px solid #eee;
}

.chat-input-container {
  display: flex;
  align-items: flex-end;
  gap: 10px;
  background: #f5f5f5;
  border-radius: 24px;
  padding: 8px 8px 8px 16px;
}

.chat-input {
  flex: 1;
  border: none;
  background: transparent;
  resize: none;
  outline: none;
  font-size: 15px;
  line-height: 1.5;
  color: #333;
  min-height: 24px;
  max-height: 120px;
  overflow-y: auto;
}

.chat-input::placeholder {
  color: #999;
}

.chat-input:disabled {
  opacity: 0.6;
}

.send-btn {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  border: none;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
  flex-shrink: 0;
}

.send-btn:hover:not(.disabled) {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.send-btn.disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.send-icon {
  font-size: 18px;
  font-weight: bold;
}

.input-hint {
  text-align: center;
  font-size: 11px;
  color: #bbb;
  margin-top: 6px;
}
</style>