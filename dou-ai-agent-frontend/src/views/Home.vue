<template>
  <div class="home-page">
    <div class="home-header">
      <h1 class="app-title">AI 智能助手</h1>
      <p class="app-subtitle">选择您想要的应用开始对话</p>
    </div>

    <div class="app-cards">
      <div class="app-card" @click="goTo('/love-app')">
        <div class="card-icon">💕</div>
        <h2 class="card-title">AI 恋爱大师</h2>
        <p class="card-desc">专业的情感顾问，为您解答恋爱中的困惑与问题</p>
        <div class="card-tag">情感陪伴</div>
      </div>

      <div class="app-card" @click="goTo('/manus-app')">
        <div class="card-icon">🤖</div>
        <h2 class="card-title">AI 超级智能体</h2>
        <p class="card-desc">强大的 AI 智能体，助您完成各类复杂任务</p>
        <div class="card-tag">智能助手</div>
      </div>
    </div>

    <div class="home-footer">
      <p>Powered by Dou AI Agent</p>
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
.home-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

.home-header {
  text-align: center;
  margin-bottom: 50px;
}

.app-title {
  font-size: 42px;
  font-weight: 700;
  color: #fff;
  margin: 0 0 12px;
  letter-spacing: 2px;
}

.app-subtitle {
  font-size: 16px;
  color: rgba(255, 255, 255, 0.8);
  margin: 0;
}

.app-cards {
  display: flex;
  gap: 30px;
  max-width: 900px;
  width: 100%;
  justify-content: center;
  flex-wrap: wrap;
}

.app-card {
  background: #fff;
  border-radius: 20px;
  padding: 40px 30px;
  width: 360px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15);
  position: relative;
  overflow: hidden;
}

.app-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
}

.app-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.25);
}

.card-icon {
  font-size: 60px;
  margin-bottom: 20px;
}

.card-title {
  font-size: 22px;
  font-weight: 600;
  color: #333;
  margin: 0 0 12px;
}

.card-desc {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  margin: 0 0 20px;
}

.card-tag {
  display: inline-block;
  padding: 4px 12px;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
  color: #667eea;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.home-footer {
  margin-top: 50px;
  color: rgba(255, 255, 255, 0.6);
  font-size: 13px;
}
</style>