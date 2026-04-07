import { createRouter, createWebHistory } from 'vue-router'
import { useHead, websiteJsonLd, webAppJsonLd } from '@/composables/useHead'
import Home    from '@/views/Home.vue'
import LoveApp from '@/views/LoveApp.vue'
import ManusApp from '@/views/ManusApp.vue'

// ── 每个路由的 SEO 配置 ──────────────────────────────
const SEO_MAP = {
  Home: {
    title:       '首页',
    description: 'AI 智能助手平台，提供 AI 恋爱大师、AI 超级智能体等对话服务，助力情感分析与复杂任务处理',
    keywords:    'AI助手,AI聊天,智能对话,恋爱大师,AI智能体,人工智能,情感分析',
    canonical:   'https://douaiagent.com',
    ogImage:     'https://douaiagent.com/og-home.png',
    jsonLd:      websiteJsonLd(),
  },
  LoveApp: {
    title:       'AI 恋爱大师',
    description: '专业的 AI 恋爱大师，情感顾问机器人，帮助您分析恋爱困惑、处理感情问题、给出关系建议',
    keywords:    'AI恋爱,恋爱大师,情感分析,AI情感顾问,恋爱咨询,两性关系',
    canonical:   'https://douaiagent.com/love-app',
    ogImage:     'https://douaiagent.com/og-love.png',
    jsonLd:      webAppJsonLd('AI 恋爱大师', '专业的 AI 情感顾问机器人'),
  },
  ManusApp: {
    title:       'AI 超级智能体',
    description: '强大的 AI 超级智能体，基于大模型的通用 AI 助手，助您完成信息查询、写作、分析等复杂任务',
    keywords:    'AI超级智能体,AI助手,AI智能体,大模型助手,任务助手,AI写作,AI分析',
    canonical:   'https://douaiagent.com/manus-app',
    ogImage:     'https://douaiagent.com/og-manus.png',
    jsonLd:      webAppJsonLd('AI 超级智能体', '强大的通用 AI 智能体助手'),
  },
}

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home,
    meta: SEO_MAP.Home,
  },
  {
    path: '/love-app',
    name: 'LoveApp',
    component: LoveApp,
    meta: SEO_MAP.LoveApp,
  },
  {
    path: '/manus-app',
    name: 'ManusApp',
    component: ManusApp,
    meta: SEO_MAP.ManusApp,
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return { top: 0 }
  },
})

// ── 全局 beforeEach：路由切换时自动更新 SEO head ──
router.beforeEach((to) => {
  const seo = to.meta || {}
  useHead({
    title:       seo.title,
    description: seo.description,
    keywords:    seo.keywords,
    canonical:   seo.canonical,
    ogImage:     seo.ogImage,
    jsonLd:      seo.jsonLd,
  })
})

export default router
