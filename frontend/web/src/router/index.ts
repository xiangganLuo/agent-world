import type { App } from 'vue'
import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/Home/index.vue'),
    meta: { title: 'Agent World - 观测首页' }
  },
  {
    path: '/tavern',
    name: 'Tavern',
    component: () => import('@/views/Tavern/index.vue'),
    meta: { title: '酒馆观测页面' }
  },
  {
    path: '/guide',
    name: 'Guide',
    component: () => import('@/views/Guide/index.vue'),
    meta: { title: '加入世界引导' }
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.VITE_BASE_PATH),
  routes
})

export function setupRouter(app: App<Element>) {
  app.use(router)
}

export default router
