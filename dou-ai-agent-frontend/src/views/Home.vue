<template>
  <div class="home-page">
    <!-- 背景装饰 -->
    <div class="bg-orb orb-1"></div>
    <div class="bg-orb orb-2"></div>
    <div class="bg-orb orb-3"></div>

    <div class="home-content">
      <!-- 标题区 -->
      <header class="home-header">
        <div class="header-icon">
          <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <circle cx="12" cy="12" r="10"/>
            <path d="M8 14s1.5 2 4 2 4-2 4-2"/>
            <line x1="9" y1="9" x2="9.01" y2="9"/>
            <line x1="15" y1="9" x2="15.01" y2="9"/>
          </svg>
        </div>
        <h1 class="home-title">AI 智能助手</h1>
        <p class="home-subtitle">选择下方应用，开启智能对话之旅</p>
      </header>

      <!-- 应用卡片网格 -->
      <div class="app-grid">
        <!-- AI 恋爱大师 -->
        <div class="app-card card-love" @click="goTo('/love-app')">
          <div class="card-bg-effect"></div>
          <div class="card-top-bar bar-love"></div>
          <div class="card-icon-wrap">
            <svg width="36" height="36" viewBox="0 0 24 24" fill="currentColor">
              <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"/>
            </svg>
          </div>
          <h2 class="card-title">AI 恋爱大师</h2>
          <p class="card-desc">专业的情感顾问，为您解答恋爱中的困惑与问题</p>
          <div class="card-footer">
            <span class="card-tag tag-love">情感陪伴</span>
            <span class="card-arrow">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                <line x1="5" y1="12" x2="19" y2="12"/>
                <polyline points="12 5 19 12 12 19"/>
              </svg>
            </span>
          </div>
        </div>

        <!-- AI 超级智能体 -->
        <div class="app-card card-manus" @click="goTo('/manus-app')">
          <div class="card-bg-effect"></div>
          <div class="card-top-bar bar-manus"></div>
          <div class="card-icon-wrap">
            <svg width="36" height="36" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/>
            </svg>
          </div>
          <h2 class="card-title">AI 超级智能体</h2>
          <p class="card-desc">强大的 AI 智能体，助您完成各类复杂任务</p>
          <div class="card-footer">
            <span class="card-tag tag-manus">智能助手</span>
            <span class="card-arrow">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                <line x1="5" y1="12" x2="19" y2="12"/>
                <polyline points="12 5 19 12 12 19"/>
              </svg>
            </span>
          </div>
        </div>
      </div>

      <!-- 底部信息 -->
      <footer class="home-footer">
        <span class="footer-dot"></span>
        <span>Powered by Dou AI Agent</span>
        <span class="footer-dot"></span>
      </footer>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

function goTo(path) {
  router.push(path)
}
</script>

<style scoped>
/* ── 页面容器 ── */
.home-page {
  min-height: 100dvh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  background: #0f0c29;
  background: linear-gradient(135deg, #0f0c29 0%, #1b1f3a 50%, #201d3a 100%);
  position: relative;
  overflow: hidden;
}

/* ── 背景光斑 ── */
.bg-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  pointer-events: none;
}

.orb-1 {
  width: 500px;
  height: 500px;
  background: radial-gradient(circle, rgba(99, 102, 241, 0.22) 0%, transparent 70%);
  top: -120px;
  right: -100px;
  animation: orbFloat 8s ease-in-out infinite;
}

.orb-2 {
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, rgba(236, 72, 153, 0.18) 0%, transparent 70%);
  bottom: -80px;
  left: -80px;
  animation: orbFloat 10s ease-in-out infinite reverse;
}

.orb-3 {
  width: 300px;
  height: 300px;
  background: radial-gradient(circle, rgba(59, 130, 246, 0.14) 0%, transparent 70%);
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  animation: orbPulse 6s ease-in-out infinite;
}

@keyframes orbFloat {
  0%, 100% { transform: translate(0, 0); }
  50%       { transform: translate(-20px, 20px); }
}

@keyframes orbPulse {
  0%, 100% { opacity: 0.6; transform: translate(-50%, -50%) scale(1); }
  50%       { opacity: 1;   transform: translate(-50%, -50%) scale(1.15); }
}

/* ── 内容层 ── */
.home-content {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 48px;
  max-width: 900px;
  width: 100%;
}

/* ── 标题区 ── */
.home-header {
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}

.header-icon {
  width: 72px;
  height: 72px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.07);
  border: 1px solid rgba(255, 255, 255, 0.12);
  display: flex;
  align-items: center;
  justify-content: center;
  color: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(8px);
  box-shadow: 0 8px 32px rgba(99, 102, 241, 0.2);
  animation: headerIconIn 0.6s cubic-bezier(0.34, 1.56, 0.64, 1) both;
}

.home-title {
  font-size: clamp(28px, 5vw, 44px);
  font-weight: 700;
  color: #fff;
  letter-spacing: 3px;
  animation: fadeUp 0.5s ease both 0.1s;
}

.home-subtitle {
  font-size: 15px;
  color: rgba(255, 255, 255, 0.5);
  letter-spacing: 1px;
  animation: fadeUp 0.5s ease both 0.2s;
}

/* ── 应用网格 ── */
.app-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;
  width: 100%;
}

/* ── 应用卡片 ── */
.app-card {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 24px;
  padding: 36px 30px 28px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  gap: 12px;
  backdrop-filter: blur(16px);
  transition: transform var(--transition-normal), box-shadow var(--transition-normal), border-color var(--transition-normal);
  animation: cardIn 0.5s cubic-bezier(0.34, 1.56, 0.64, 1) both;
}

.app-card:nth-child(1) { animation-delay: 0.15s; }
.app-card:nth-child(2) { animation-delay: 0.25s; }

.app-card:hover {
  transform: translateY(-6px) scale(1.01);
  border-color: rgba(255, 255, 255, 0.22);
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.4);
}

/* 卡片顶部强调条 */
.card-top-bar {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  border-radius: 24px 24px 0 0;
}

.bar-love  { background: linear-gradient(90deg, #ec4899, #f43f5e); }
.bar-manus { background: linear-gradient(90deg, #3b82f6, #6366f1, #8b5cf6); }

/* 卡片图标容器 */
.card-icon-wrap {
  width: 64px;
  height: 64px;
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 4px;
  transition: transform var(--transition-normal);
}

.app-card:hover .card-icon-wrap {
  transform: scale(1.1) rotate(-4deg);
}

.card-love .card-icon-wrap {
  background: linear-gradient(135deg, rgba(236, 72, 153, 0.2) 0%, rgba(244, 63, 94, 0.15) 100%);
  color: #fb7185;
  border: 1px solid rgba(236, 72, 153, 0.25);
}

.card-manus .card-icon-wrap {
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.2) 0%, rgba(99, 102, 241, 0.15) 100%);
  color: #818cf8;
  border: 1px solid rgba(59, 130, 246, 0.25);
}

.card-title {
  font-size: 19px;
  font-weight: 600;
  color: #fff;
  margin: 0;
  letter-spacing: 0.5px;
}

.card-desc {
  font-size: 13.5px;
  color: rgba(255, 255, 255, 0.48);
  line-height: 1.65;
  margin: 0;
  flex: 1;
}

/* 卡片底部 */
.card-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 4px;
}

.card-tag {
  display: inline-flex;
  align-items: center;
  padding: 4px 12px;
  border-radius: var(--radius-full);
  font-size: 12px;
  font-weight: 500;
  letter-spacing: 0.3px;
}

.tag-love {
  background: rgba(236, 72, 153, 0.15);
  color: #fb7185;
  border: 1px solid rgba(236, 72, 153, 0.25);
}

.tag-manus {
  background: rgba(59, 130, 246, 0.15);
  color: #818cf8;
  border: 1px solid rgba(59, 130, 246, 0.25);
}

.card-arrow {
  color: rgba(255, 255, 255, 0.3);
  display: flex;
  transition: transform var(--transition-normal), color var(--transition-normal);
}

.app-card:hover .card-arrow {
  transform: translateX(4px);
  color: rgba(255, 255, 255, 0.7);
}

/* ── 底部 ── */
.home-footer {
  display: flex;
  align-items: center;
  gap: 10px;
  color: rgba(255, 255, 255, 0.3);
  font-size: 12.5px;
  letter-spacing: 0.5px;
  animation: fadeUp 0.5s ease both 0.35s;
}

.footer-dot {
  width: 4px;
  height: 4px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
}

/* ── 动画 ── */
@keyframes headerIconIn {
  from { opacity: 0; transform: scale(0.7); }
  to   { opacity: 1; transform: scale(1); }
}

@keyframes fadeUp {
  from { opacity: 0; transform: translateY(16px); }
  to   { opacity: 1; transform: translateY(0); }
}

@keyframes cardIn {
  from { opacity: 0; transform: translateY(24px) scale(0.94); }
  to   { opacity: 1; transform: translateY(0) scale(1); }
}

/* ── 响应式：平板 ── */
@media (max-width: 768px) {
  .home-page {
    padding: 32px 16px;
    align-items: flex-start;
    padding-top: 60px;
  }

  .app-grid {
    gap: 18px;
  }

  .app-card {
    padding: 28px 24px 22px;
  }

  .card-icon-wrap {
    width: 56px;
    height: 56px;
  }

  .home-content {
    gap: 36px;
  }
}

/* ── 响应式：手机单列 ── */
@media (max-width: 580px) {
  .app-grid {
    grid-template-columns: 1fr;
  }

  .home-title {
    letter-spacing: 1.5px;
  }
}

/* ── 响应式：超小手机 ── */
@media (max-width: 380px) {
  .home-page {
    padding: 28px 12px;
    padding-top: 48px;
  }

  .app-card {
    padding: 24px 20px 18px;
  }
}
</style>
