<template>
  <div class="input-area">
    <div class="input-row">
      <!-- 输入框 -->
      <div class="input-field">
        <textarea
          ref="textareaRef"
          v-model="inputText"
          class="text-input"
          :placeholder="placeholder"
          :disabled="disabled"
          rows="1"
          @keydown.enter.exact.prevent="handleSend"
          @input="autoResize"
        ></textarea>
      </div>

      <!-- 发送按钮 -->
      <button
        class="send-btn"
        :class="{ 'is-active': canSend }"
        :disabled="!canSend"
        @click="handleSend"
        title="发送"
      >
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
          <line x1="22" y1="2" x2="11" y2="13"/>
          <polygon points="22 2 15 22 11 13 2 9 22 2"/>
        </svg>
      </button>
    </div>

    <!-- 提示文字 -->
    <p class="input-tip">按 Enter 发送，Shift + Enter 换行</p>
  </div>
</template>

<script setup>
import { ref, computed, nextTick } from 'vue'

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

const canSend = computed(() => {
  return inputText.value.trim().length > 0 && !props.disabled
})

function handleSend() {
  if (!canSend.value) return
  emit('send', inputText.value.trim())
  inputText.value = ''
  nextTick(() => {
    if (textareaRef.value) {
      textareaRef.value.style.height = 'auto'
    }
  })
}

function autoResize() {
  const el = textareaRef.value
  if (!el) return
  el.style.height = 'auto'
  const maxH = 120
  el.style.height = Math.min(el.scrollHeight, maxH) + 'px'
}
</script>

<style scoped>
.input-area {
  padding: 10px 14px 12px;
  background: var(--color-surface);
  border-top: 1px solid var(--color-border);
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.input-row {
  display: flex;
  align-items: flex-end;
  gap: 10px;
}

/* ── 输入框 ── */
.input-field {
  flex: 1;
  background: var(--color-bg);
  border: 1.5px solid var(--color-border);
  border-radius: var(--radius-xl);
  padding: 10px 16px;
  transition: border-color var(--transition-fast), box-shadow var(--transition-fast);
  display: flex;
  align-items: flex-end;
}

.input-field:focus-within {
  border-color: var(--color-primary-light);
  box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.1);
}

.text-input {
  width: 100%;
  border: none;
  background: transparent;
  resize: none;
  outline: none;
  font-family: var(--font-family);
  font-size: 15px;
  line-height: 1.6;
  color: var(--color-text-primary);
  min-height: 24px;
  max-height: 120px;
  overflow-y: auto;
}

.text-input::placeholder {
  color: var(--color-text-muted);
}

.text-input:disabled {
  opacity: 0.55;
  cursor: not-allowed;
}

/* ── 发送按钮 ── */
.send-btn {
  width: 42px;
  height: 42px;
  border-radius: var(--radius-full);
  border: none;
  background: var(--color-border);
  color: var(--color-text-muted);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  transition: all var(--transition-normal);
  padding: 0;
}

.send-btn.is-active {
  background: var(--color-bubble-user);
  color: #fff;
  box-shadow: 0 4px 14px rgba(99, 102, 241, 0.4);
  transform: scale(1);
}

.send-btn.is-active:hover {
  transform: scale(1.06);
  box-shadow: 0 6px 20px rgba(99, 102, 241, 0.5);
}

.send-btn.is-active:active {
  transform: scale(0.96);
}

.send-btn:disabled {
  cursor: not-allowed;
}

/* ── 提示文字 ── */
.input-tip {
  text-align: center;
  font-size: 11px;
  color: var(--color-text-muted);
  letter-spacing: 0.2px;
}

/* ── 响应式 ── */
@media (max-width: 480px) {
  .input-area {
    padding: 8px 10px 10px;
  }

  .input-field {
    padding: 9px 14px;
  }

  .send-btn {
    width: 38px;
    height: 38px;
  }

  .text-input {
    font-size: 14px;
  }
}
</style>
