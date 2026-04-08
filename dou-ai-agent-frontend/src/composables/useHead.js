/**
 * useHead — 轻量级 SEO head 管理 composable
 *
 * 用法：
 *   import { useHead } from '@/composables/useHead'
 *   useHead({ title, description, ... })
 *
 * 也可在 router 的 beforeEach 里集中调用，参考 router/index.js
 */

// 全站默认 SEO 配置（可被页面级配置覆盖）
const SITE_DEFAULT = {
  siteName: 'Dou AI Agent',
  siteFullName: 'AI 智能助手',
  siteUrl: import.meta.env.VITE_SITE_URL || 'https://example.com',
  siteDescription: 'AI 智能助手平台，提供 AI 恋爱大师、AI 超级智能体等对话服务',
  siteKeywords: 'AI助手,AI聊天,智能对话,恋爱大师,AI智能体',
  ogImage: import.meta.env.VITE_OG_IMAGE || '/og-default.png',
}

/**
 * 更新 document.title
 * @param {string} title
 */
export function setPageTitle(title) {
  document.title = title ? `${title} - ${SITE_DEFAULT.siteFullName}` : SITE_DEFAULT.siteFullName
}

/**
 * 设置/追加单个 <meta> 标签
 * @param {string} name      meta name 或 property（如 'description'、'og:title'）
 * @param {string} content   内容
 * @param {string} type      'name' | 'property'
 */
function upsertMeta(name, content, type = 'name') {
  const attrName = type === 'property' ? 'property' : 'name'
  let el = document.querySelector(`meta[${attrName}="${name}"]`)
  if (!el) {
    el = document.createElement('meta')
    el.setAttribute(attrName, name)
    document.head.appendChild(el)
  }
  el.setAttribute('content', content)
}

/**
 * 删除指定 name 或 property 的 meta 标签
 */
function removeMeta(name, type = 'name') {
  const attrName = type === 'property' ? 'property' : 'name'
  const el = document.querySelector(`meta[${attrName}="${name}"]`)
  if (el) el.remove()
}

/**
 * 设置/替换 <link rel="canonical">
 */
function setCanonical(url) {
  let link = document.querySelector('link[rel="canonical"]')
  if (!link) {
    link = document.createElement('link')
    link.rel = 'canonical'
    document.head.appendChild(link)
  }
  link.href = url
}

/**
 * 设置 JSON-LD 结构化数据
 */
function setJsonLd(schema) {
  removeJsonLd()
  const script = document.createElement('script')
  script.type = 'application/ld+json'
  script.id = 'seo-json-ld'
  script.textContent = JSON.stringify(schema)
  document.head.appendChild(script)
}

function removeJsonLd() {
  const old = document.getElementById('seo-json-ld')
  if (old) old.remove()
}

/**
 * 完整 SEO head 更新
 *
 * @param {Object} opts
 * @param {string}   [opts.title]              页面标题（不含站点名，自动拼接）
 * @param {string}   [opts.description]        Meta description
 * @param {string}   [opts.keywords]            Meta keywords（逗号分隔）
 * @param {string}   [opts.ogImage]             社交分享大图（完整 URL）
 * @param {string}   [opts.canonical]          规范 URL（完整 URL）
 * @param {string}   [opts.ogType]              og:type（website | article | product 等）
 * @param {boolean}  [opts.noindex]            是否禁止收录（noindex）
 * @param {Object}   [opts.jsonLd]             JSON-LD schema 对象（可选）
 */
export function useHead(opts = {}) {
  const {
    title,
    description = SITE_DEFAULT.siteDescription,
    keywords    = SITE_DEFAULT.siteKeywords,
    ogImage,
    canonical,
    ogType = 'website',
    noindex = false,
    jsonLd,
  } = opts

  const fullTitle = title ? `${title} - ${SITE_DEFAULT.siteFullName}` : SITE_DEFAULT.siteFullName
  const pageUrl   = canonical || (SITE_DEFAULT.siteUrl + window.location.pathname)
  const shareImg  = ogImage    || (SITE_DEFAULT.siteUrl + SITE_DEFAULT.ogImage)

  // ── 基础 Meta ──
  document.title = fullTitle
  upsertMeta('description',    description)
  upsertMeta('keywords',        keywords)
  upsertMeta('author',          SITE_DEFAULT.siteName)

  // ── Robots ──
  upsertMeta('robots', noindex ? 'noindex, nofollow' : 'index, follow')

  // ── Open Graph ──
  upsertMeta('og:title',       fullTitle,   'property')
  upsertMeta('og:description', description,  'property')
  upsertMeta('og:type',        ogType,       'property')
  upsertMeta('og:url',         pageUrl,      'property')
  upsertMeta('og:image',       shareImg,     'property')
  upsertMeta('og:site_name',   SITE_DEFAULT.siteFullName, 'property')

  // ── Twitter Card ──
  upsertMeta('twitter:card',        'summary_large_image', 'name')
  upsertMeta('twitter:title',       fullTitle,             'name')
  upsertMeta('twitter:description', description,            'name')
  upsertMeta('twitter:image',       shareImg,              'name')
  upsertMeta('twitter:site',        '@YourTwitterHandle',   'name')

  // ── 移动端兼容 ──
  upsertMeta('format-detection', 'telephone=no')

  // ── Canonical ──
  if (canonical) setCanonical(canonical)

  // ── JSON-LD ──
  if (jsonLd) setJsonLd(jsonLd)
  else        removeJsonLd()
}

/**
 * 生成 WebSite JSON-LD（首页用）
 */
export function websiteJsonLd() {
  return {
    '@context': 'https://schema.org',
    '@type': 'WebSite',
    name:     SITE_DEFAULT.siteFullName,
    url:      SITE_DEFAULT.siteUrl,
    description: SITE_DEFAULT.siteDescription,
    potentialAction: {
      '@type': 'SearchAction',
      target: {
        '@type': 'EntryPoint',
        urlTemplate: `${SITE_DEFAULT.siteUrl}/?q={search_term_string}`,
      },
      'query-input': 'required name=search_term_string',
    },
  }
}

/**
 * 生成 WebApplication JSON-LD（单页应用用）
 */
export function webAppJsonLd(name, description) {
  return {
    '@context': 'https://schema.org',
    '@type': 'WebApplication',
    name,
    description,
    url:        SITE_DEFAULT.siteUrl,
    applicationCategory: 'LifestyleApplication',
    operatingSystem: 'Web Browser',
    offers: {
      '@type': 'Offer',
      price: '0',
      priceCurrency: 'CNY',
    },
  }
}
