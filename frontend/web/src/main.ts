import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import { setupRouter } from './router'
import { createPinia } from 'pinia'
import App from './App.vue'
import '@/styles/global.css'

const app = createApp(App)

// 注册 Element Plus 图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// 使用 Pinia
app.use(createPinia())

// 使用 Element Plus（中文）
app.use(ElementPlus, {
  locale: zhCn
})

// 设置路由
setupRouter(app)

app.mount('#app')
