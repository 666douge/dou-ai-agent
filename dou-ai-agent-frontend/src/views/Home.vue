<template>
  <div class="home">
    <!-- 背景层 -->
    <canvas ref="gridCanvas" class="grid-canvas"></canvas>
    <div class="noise-overlay"></div>
    <div class="scanlines"></div>

    <!-- 粒子层 -->
    <div class="particles" ref="particlesRef">
      <span v-for="n in 18" :key="n" class="particle" :style="particleStyle(n)"></span>
    </div>

    <!-- 主内容 -->
    <main class="content">

      <!-- Hero 区 -->
      <section class="hero">
        <!-- Logo 标 -->
        <div class="logo-mark" ref="logoRef">
          <svg width="56" height="56" viewBox="0 0 56 56" fill="none">
            <defs>
              <linearGradient id="logoGrad" x1="0" y1="0" x2="56" y2="56" gradientUnits="userSpaceOnUse">
                <stop offset="0%" stop-color="#00f5ff"/>
                <stop offset="100%" stop-color="#bf5af2"/>
              </linearGradient>
              <filter id="logoGlow">
                <feGaussianBlur stdDeviation="3" result="blur"/>
                <feMerge><feMergeNode in="blur"/><feMergeNode in="SourceGraphic"/></feMerge>
              </filter>
            </defs>
            <polygon points="28,4 52,16 52,40 28,52 4,40 4,16" stroke="url(#logoGrad)" stroke-width="1.5" fill="none" filter="url(#logoGlow)"/>
            <polygon points="28,12 44,20 44,36 28,44 12,36 12,20" stroke="url(#logoGrad)" stroke-width="1" fill="rgba(0,245,255,0.04)"/>
            <text x="28" y="33" text-anchor="middle" font-family="monospace" font-size="14" font-weight="bold" fill="url(#logoGrad)">AI</text>
          </svg>
        </div>

        <!-- 主标题（带 glitch） -->
        <h1 class="title-glitch" data-text="DOU AI AGENT">DOU AI AGENT</h1>

        <!-- 副标题终端行 -->
        <div class="terminal-line">
          <span class="prompt">›</span>
          <span class="cmd">select mode from intelligence</span>
          <span class="cursor">_</span>
        </div>

        <!-- 标语 -->
        <p class="tagline">超越想象 · 驾驭未来</p>
      </section>

      <!-- 应用入口区 -->
      <section class="apps">
        <!-- AI 恋爱大师 -->
        <div class="card card-love" @click="goTo('/love-app')">
          <div class="card-glow"></div>
          <div class="card-inner">
            <div class="card-header">
              <div class="card-icon">
                <svg width="28" height="28" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"/>
                </svg>
              </div>
              <div class="card-status">
                <span class="status-dot"></span>
                <span class="status-text">ONLINE</span>
              </div>
            </div>

            <div class="card-body">
              <h2 class="card-name">AI 恋爱大师</h2>
              <p class="card-desc">深度情感引擎 · 精准共情算法<br>专业关系顾问 · 随时在线</p>
            </div>

            <div class="card-footer">
              <span class="card-tag">EMOTION ENGINE v2.1</span>
              <div class="card-enter">
                <span>ENTER</span>
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
                  <line x1="5" y1="12" x2="19" y2="12"/>
                  <polyline points="12 5 19 12 12 19"/>
                </svg>
              </div>
            </div>
          </div>
        </div>

        <!-- AI 超级智能体 -->
        <div class="card card-manus" @click="goTo('/manus-app')">
          <div class="card-glow"></div>
          <div class="card-inner">
            <div class="card-header">
              <div class="card-icon">
                <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/>
                </svg>
              </div>
              <div class="card-status">
                <span class="status-dot"></span>
                <span class="status-text">ONLINE</span>
              </div>
            </div>

            <div class="card-body">
              <h2 class="card-name">AI 超级智能体</h2>
              <p class="card-desc">多模态推理 · 自主任务规划<br>工具链编排 · 无限扩展</p>
            </div>

            <div class="card-footer">
              <span class="card-tag">AGENT CORE v3.0</span>
              <div class="card-enter">
                <span>ENTER</span>
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
                  <line x1="5" y1="12" x2="19" y2="12"/>
                  <polyline points="12 5 19 12 12 19"/>
                </svg>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- 底部版权 -->
      <footer class="footer">
        <span>DOU AI AGENT</span>
        <span class="sep">·</span>
        <span>POWERED BY LARGE LANGUAGE MODEL</span>
      </footer>

    </main>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'

const router  = useRouter()
const gridCanvas = ref(null)
const particlesRef = ref(null)
let gridAnimId = null

function goTo(path) { router.push(path) }

function particleStyle(n) {
  const size   = 2 + (n % 3)
  const delay  = (n * 0.37) % 3
  const dur    = 4 + (n % 5)
  const left   = ((n * 53.7) % 97).toFixed(1)
  const top    = ((n * 41.3) % 92).toFixed(1)
  const hue    = 180 + (n * 23) % 120
  return {
    width:  size + 'px',
    height: size + 'px',
    left:   left + '%',
    top:    top  + '%',
    background: `hsl(${hue}, 100%, 70%)`,
    animationDelay: delay + 's',
    animationDuration: dur + 's',
  }
}

// ── 透视网格动画 ──
function initGrid() {
  const canvas = gridCanvas.value
  if (!canvas) return
  const ctx = canvas.getContext('2d')

  function resize() {
    canvas.width  = window.innerWidth
    canvas.height = window.innerHeight
  }
  resize()
  window.addEventListener('resize', resize)

  let t = 0
  function drawGrid() {
    const w = canvas.width
    const h = canvas.height
    ctx.clearRect(0, 0, w, h)

    // 透视参数
    const horizon  = h * 0.42
    const vanishX  = w * 0.5
    const gridCols = 28
    const gridRows = 18
    const cellW    = w / gridCols
    const cellH    = h * 0.06

    ctx.lineWidth = 0.6

    // 竖线（向消失点汇聚）
    for (let i = 0; i <= gridCols; i++) {
      const x = i * cellW
      const alpha = Math.max(0, 0.18 - Math.abs(x - vanishX) / w * 0.18)
      ctx.strokeStyle = `rgba(0,245,255,${alpha})`
      ctx.beginPath()
      ctx.moveTo(x, horizon)
      ctx.lineTo(x < vanishX ? 0       : w, 0)
      ctx.stroke()
      ctx.beginPath()
      ctx.moveTo(x, horizon)
      ctx.lineTo(x < vanishX ? 0       : w, h)
      ctx.stroke()
    }

    // 横线（从地平线向下扩散）
    const speed = 0.018
    for (let j = 0; j <= gridRows; j++) {
      const raw = j / gridRows
      // 加速效果：底部间距大，顶部间距小
      const compressed = Math.pow(raw, 1.6)
      const y = horizon + (h - horizon) * compressed + (t * (1 + j * 0.3)) % (cellH * 1.5)
      const clampedY  = Math.min(y, h)
      const alpha     = Math.max(0, 0.14 - clampedY / h * 0.14)
      const color     = clampedY > horizon
        ? `rgba(0,245,255,${alpha})`
        : `rgba(191,90,242,${alpha * 0.5})`
      ctx.strokeStyle = color
      ctx.beginPath()
      ctx.moveTo(0,     clampedY)
      ctx.lineTo(w,     clampedY)
      ctx.stroke()
    }

    // 地平线光晕
    const grad = ctx.createLinearGradient(0, horizon - 30, 0, horizon + 30)
    grad.addColorStop(0,   'rgba(191,90,242,0)')
    grad.addColorStop(0.5, 'rgba(191,90,242,0.12)')
    grad.addColorStop(1,   'rgba(0,245,255,0)')
    ctx.fillStyle = grad
    ctx.fillRect(0, horizon - 30, w, 60)

    t += speed
    gridAnimId = requestAnimationFrame(drawGrid)
  }

  drawGrid()
}

onMounted(() => {
  initGrid()
})

onUnmounted(() => {
  if (gridAnimId) cancelAnimationFrame(gridAnimId)
})
</script>

<style scoped>
/* ════════════════════════════════════════════
   变量 & 重置
   ════════════════════════════════════════════ */
.home {
  --neon-cyan:   #00f5ff;
  --neon-purple: #bf5af2;
  --neon-pink:   #ff2d78;
  --dark-bg:     #030308;
  --glass-bg:    rgba(255,255,255,0.03);
  --glass-border:rgba(255,255,255,0.08);

  min-height: 100dvh;
  background: var(--dark-bg);
  position: relative;
  overflow: hidden;
  font-family: 'PingFang SC', 'Microsoft YaHei', -apple-system, 'SF Mono', 'Fira Code', monospace;
  color: #fff;
}

/* ════════════════════════════════════════════
   背景层
   ════════════════════════════════════════════ */
.grid-canvas {
  position: fixed;
  inset: 0;
  z-index: 0;
  pointer-events: none;
}

.noise-overlay {
  position: fixed;
  inset: 0;
  z-index: 1;
  pointer-events: none;
  background-image: url("data:image/svg+xml,%3Csvg viewBox='0 0 256 256' xmlns='http://www.w3.org/2000/svg'%3E%3Cfilter id='n'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.9' numOctaves='4' stitchTiles='stitch'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23n)' opacity='0.04'/%3E%3C/svg%3E");
  background-size: 200px 200px;
  opacity: 0.5;
}

.scanlines {
  position: fixed;
  inset: 0;
  z-index: 2;
  pointer-events: none;
  background: repeating-linear-gradient(
    0deg,
    transparent,
    transparent 2px,
    rgba(0,0,0,0.07) 2px,
    rgba(0,0,0,0.07) 4px
  );
}

/* ════════════════════════════════════════════
   粒子
   ════════════════════════════════════════════ */
.particles {
  position: fixed;
  inset: 0;
  z-index: 1;
  pointer-events: none;
}

.particle {
  position: absolute;
  border-radius: 50%;
  opacity: 0;
  animation: particleFloat linear infinite;
  box-shadow: 0 0 6px currentColor;
}

@keyframes particleFloat {
  0%   { opacity: 0;    transform: translateY(0) scale(1); }
  10%  { opacity: 0.7; }
  90%  { opacity: 0.4; }
  100% { opacity: 0;    transform: translateY(-60px) scale(0.3); }
}

/* ════════════════════════════════════════════
   主内容
   ════════════════════════════════════════════ */
.content {
  position: relative;
  z-index: 10;
  min-height: 100dvh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 56px;
  padding: 48px 24px;
}

/* ════════════════════════════════════════════
   Hero 区
   ════════════════════════════════════════════ */
.hero {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  text-align: center;
  animation: heroIn 0.8s cubic-bezier(0.16, 1, 0.3, 1) both;
}

@keyframes heroIn {
  from { opacity: 0; transform: translateY(30px); }
  to   { opacity: 1; transform: translateY(0); }
}

/* Logo 标 */
.logo-mark {
  margin-bottom: 4px;
  filter: drop-shadow(0 0 18px rgba(0,245,255,0.5));
  animation: logoSpin 20s linear infinite;
}

@keyframes logoSpin {
  from { filter: drop-shadow(0 0 18px rgba(0,245,255,0.5)) drop-shadow(0 0 40px rgba(191,90,242,0.3)); }
  50%  { filter: drop-shadow(0 0 28px rgba(191,90,242,0.6)) drop-shadow(0 0 50px rgba(0,245,255,0.3)); }
  to   { filter: drop-shadow(0 0 18px rgba(0,245,255,0.5)) drop-shadow(0 0 40px rgba(191,90,242,0.3)); }
}

/* Glitch 标题 */
.title-glitch {
  font-family: 'SF Mono', 'Fira Code', 'Courier New', monospace;
  font-size: clamp(36px, 8vw, 80px);
  font-weight: 900;
  letter-spacing: 8px;
  color: #fff;
  position: relative;
  text-shadow:
    0 0 10px rgba(0,245,255,0.8),
    0 0 30px rgba(0,245,255,0.4),
    0 0 60px rgba(0,245,255,0.2);
  cursor: default;
  user-select: none;
}

.title-glitch::before,
.title-glitch::after {
  content: attr(data-text);
  position: absolute;
  inset: 0;
  letter-spacing: 8px;
}

.title-glitch::before {
  color: var(--neon-cyan);
  animation: glitchA 4s infinite;
  clip-path: polygon(0 20%, 100% 20%, 100% 50%, 0 50%);
}

.title-glitch::after {
  color: var(--neon-pink);
  animation: glitchB 4s infinite;
  clip-path: polygon(0 55%, 100% 55%, 100% 80%, 0 80%);
}

@keyframes glitchA {
  0%, 85%, 100% { transform: translate(0); opacity: 0; }
  86%  { transform: translate(-4px, -1px); opacity: 0.9; }
  88%  { transform: translate( 3px,  1px); opacity: 0.8; }
  90%  { transform: translate( 0);         opacity: 0; }
}

@keyframes glitchB {
  0%, 88%, 100% { transform: translate(0); opacity: 0; }
  89%  { transform: translate( 4px,  1px); opacity: 0.9; }
  91%  { transform: translate(-3px, -1px); opacity: 0.8; }
  93%  { transform: translate( 0);         opacity: 0; }
}

/* 终端行 */
.terminal-line {
  display: flex;
  align-items: center;
  gap: 6px;
  font-family: 'SF Mono', 'Fira Code', monospace;
  font-size: 14px;
  color: rgba(255,255,255,0.45);
  letter-spacing: 1.5px;
  padding: 6px 18px;
  border: 1px solid rgba(0,245,255,0.12);
  border-radius: 6px;
  background: rgba(0,245,255,0.04);
  backdrop-filter: blur(8px);
}

.prompt  { color: var(--neon-cyan); font-size: 18px; }
.cmd     { color: rgba(255,255,255,0.6); }
.cursor  {
  color: var(--neon-cyan);
  animation: blink 1.1s step-end infinite;
}
@keyframes blink {
  0%, 100% { opacity: 1; }
  50%       { opacity: 0; }
}

/* 标语 */
.tagline {
  font-size: 15px;
  color: rgba(255,255,255,0.3);
  letter-spacing: 6px;
  text-transform: uppercase;
  margin-top: 4px;
  animation: fadeUp 0.6s ease both 0.2s;
}

@keyframes fadeUp {
  from { opacity: 0; transform: translateY(8px); }
  to   { opacity: 1; transform: translateY(0); }
}

/* ════════════════════════════════════════════
   应用卡片区
   ════════════════════════════════════════════ */
.apps {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  width: 100%;
  max-width: 780px;
  animation: cardsIn 0.7s cubic-bezier(0.16, 1, 0.3, 1) both 0.25s;
}

@keyframes cardsIn {
  from { opacity: 0; transform: translateY(40px) scale(0.96); }
  to   { opacity: 1; transform: translateY(0) scale(1); }
}

/* ── 单个卡片 ── */
.card {
  position: relative;
  cursor: pointer;
  border-radius: 20px;
  padding: 2px;
  background: linear-gradient(135deg, rgba(255,255,255,0.08), rgba(255,255,255,0.02));
  transition: transform 0.3s cubic-bezier(0.34, 1.56, 0.64, 1),
              box-shadow 0.3s ease;
  animation: cardHoverIn 0.5s ease both;
}

.card:nth-child(1) { animation-delay: 0.3s; }
.card:nth-child(2) { animation-delay: 0.42s; }

@keyframes cardHoverIn {
  from { opacity: 0; transform: translateY(20px); }
  to   { opacity: 1; transform: translateY(0); }
}

.card:hover {
  transform: translateY(-8px) scale(1.02);
}

.card-love:hover  { box-shadow: 0 0 40px rgba(255,45,120,0.25), 0 20px 60px rgba(0,0,0,0.5); }
.card-manus:hover { box-shadow: 0 0 40px rgba(0,245,255,0.25),  0 20px 60px rgba(0,0,0,0.5); }

/* 卡片外发光层 */
.card-glow {
  position: absolute;
  inset: -1px;
  border-radius: 21px;
  opacity: 0;
  transition: opacity 0.3s ease;
  pointer-events: none;
}
.card-love  .card-glow { background: linear-gradient(135deg, #ff2d78, #bf5af2); }
.card-manus .card-glow { background: linear-gradient(135deg, #00f5ff, #bf5af2); }
.card:hover .card-glow { opacity: 0.6; }

/* 玻璃内容 */
.card-inner {
  background: rgba(3,3,8,0.85);
  border-radius: 18px;
  padding: 28px 26px 22px;
  display: flex;
  flex-direction: column;
  gap: 16px;
  height: 100%;
  backdrop-filter: blur(20px);
  position: relative;
  overflow: hidden;
}

.card-inner::before {
  content: '';
  position: absolute;
  top: 0; left: 0; right: 0;
  height: 1px;
  border-radius: 18px 18px 0 0;
}
.card-love  .card-inner::before { background: linear-gradient(90deg, transparent, rgba(255,45,120,0.6), transparent); }
.card-manus .card-inner::before { background: linear-gradient(90deg, transparent, rgba(0,245,255,0.6), transparent); }

/* 卡片头部 */
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.card-icon {
  width: 52px;
  height: 52px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.3s ease;
}

.card-love  .card-icon {
  background: rgba(255,45,120,0.12);
  border: 1px solid rgba(255,45,120,0.25);
  color: #ff2d78;
  box-shadow: 0 0 20px rgba(255,45,120,0.15);
}

.card-manus .card-icon {
  background: rgba(0,245,255,0.08);
  border: 1px solid rgba(0,245,255,0.2);
  color: var(--neon-cyan);
  box-shadow: 0 0 20px rgba(0,245,255,0.1);
}

.card:hover .card-icon {
  transform: scale(1.1) rotate(-5deg);
}

/* 状态指示 */
.card-status {
  display: flex;
  align-items: center;
  gap: 6px;
}

.status-dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: #00ff88;
  box-shadow: 0 0 8px #00ff88;
  animation: statusPulse 2s ease-in-out infinite;
}

@keyframes statusPulse {
  0%, 100% { opacity: 1; box-shadow: 0 0 8px #00ff88; }
  50%       { opacity: 0.5; box-shadow: 0 0 3px #00ff88; }
}

.status-text {
  font-family: 'SF Mono', monospace;
  font-size: 10px;
  letter-spacing: 2px;
  color: rgba(255,255,255,0.3);
}

/* 卡片正文 */
.card-body {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.card-name {
  font-size: 20px;
  font-weight: 700;
  color: #fff;
  margin: 0;
  letter-spacing: 0.5px;
}

.card-desc {
  font-size: 13px;
  color: rgba(255,255,255,0.38);
  line-height: 1.7;
  margin: 0;
}

/* 卡片底部 */
.card-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: 8px;
  border-top: 1px solid rgba(255,255,255,0.05);
}

.card-tag {
  font-family: 'SF Mono', monospace;
  font-size: 10px;
  letter-spacing: 1.5px;
  color: rgba(255,255,255,0.2);
}

.card-enter {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 2px;
  opacity: 0;
  transform: translateX(-6px);
  transition: opacity 0.25s ease, transform 0.25s ease;
}

.card-love  .card-enter { color: #ff2d78; }
.card-manus .card-enter { color: var(--neon-cyan); }

.card:hover .card-enter {
  opacity: 1;
  transform: translateX(0);
}

/* ════════════════════════════════════════════
   底部
   ════════════════════════════════════════════ */
.footer {
  display: flex;
  align-items: center;
  gap: 10px;
  font-family: 'SF Mono', monospace;
  font-size: 11px;
  letter-spacing: 2px;
  color: rgba(255,255,255,0.15);
  animation: fadeUp 0.5s ease both 0.5s;
}

.sep { opacity: 0.4; }

/* ════════════════════════════════════════════
   响应式
   ════════════════════════════════════════════ */
@media (max-width: 768px) {
  .content {
    gap: 44px;
    padding: 48px 16px 36px;
    justify-content: flex-start;
    padding-top: 72px;
  }

  .apps {
    gap: 16px;
  }

  .card-inner {
    padding: 22px 20px 18px;
    gap: 14px;
  }

  .card-icon {
    width: 46px;
    height: 46px;
  }
}

@media (max-width: 560px) {
  .apps {
    grid-template-columns: 1fr;
    max-width: 400px;
  }

  .title-glitch {
    letter-spacing: 4px;
    font-size: 36px;
  }

  .terminal-line {
    font-size: 12px;
    padding: 5px 14px;
  }

  .tagline {
    letter-spacing: 3px;
    font-size: 13px;
  }
}

@media (max-width: 380px) {
  .content {
    padding: 52px 12px 28px;
    gap: 36px;
  }
}
</style>
