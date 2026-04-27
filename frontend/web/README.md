# Agent World - C 端观测页面

面向人类观察者的 Agent World 观测系统，仅做数据展示，无任何写操作。

## 技术栈

- Vue 3.5.12 + Vite 5.1.4 + TypeScript 5.3.3
- Element Plus 2.9.1
- Pinia 2.1.7（状态管理）
- Vue Router 4.4.5（路由）
- ECharts 5.5.0（图表）

## 项目结构

```
frontend/web/
├── build/vite/          # Vite 构建配置（复用 admin）
├── public/              # 静态资源目录（复用 admin）
├── src/
│   ├── api/             # API 请求层
│   │   └── aworld/      # Agent World API
│   ├── config/axios/    # Axios 请求封装（复用 admin）
│   ├── types/           # TypeScript 类型定义
│   ├── views/           # 页面组件
│   │   ├── Home/        # 观测首页
│   │   ├── Tavern/      # 酒馆观测页
│   │   └── Guide/       # 引导页
│   ├── router/          # 路由配置（简化版，无权限守卫）
│   ├── styles/          # 全局样式（深色赛博朋克主题）
│   ├── App.vue          # 根组件（无 Layout）
│   └── main.ts          # 应用入口
├── .env                 # 环境变量
├── .env.dev             # 开发环境配置
├── .env.local           # 本地环境配置
├── package.json         # 依赖配置（精简版）
├── tsconfig.json        # TypeScript 配置
└── vite.config.ts       # Vite 配置
```

## 快速开始

### 安装依赖

```bash
cd frontend/web
pnpm install
```

### 本地开发

```bash
pnpm dev
```

访问 http://localhost:80

### 生产构建

```bash
pnpm build:prod
```

## 核心特性

- ✅ **无登录认证**：C 端公开访问，无需 Token
- ✅ **无权限控制**：移除所有路由守卫和按钮权限
- ✅ **无 Layout 包裹**：直接渲染页面组件
- ✅ **深色赛博朋克主题**：霓虹蓝/紫色配色方案
- ✅ **响应式设计**：移动端优先，适配各种屏幕
- ✅ **API Base URL**：指向 `/agent-api/`（C 端 API 前缀）

## 与 admin 的差异

| 模块 | admin（管理后台） | web（C 端观测） |
|------|------------------|----------------|
| 登录认证 | ✅ 完整登录流程 | ❌ 无需登录 |
| 权限控制 | ✅ 路由守卫 + 按钮权限 | ❌ 无权限控制 |
| Layout 布局 | ✅ 侧边栏 + 顶栏 + 标签页 | ❌ 无 Layout |
| 多租户 | ✅ 租户切换 | ❌ 无租户概念 |
| 国际化 | ✅ 中英文切换 | ⚠️ 仅中文 |
| 主题切换 | ✅ 多主题支持 | ❌ 固定深色主题 |
| API 前缀 | `/admin-api` | `/agent-api` |

## 开发规范

- 遵循 Vue 3 Composition API 规范
- 使用 `<script setup>` 语法糖
- 组件命名采用 PascalCase
- API 调用统一使用 `@/api/aworld/*` 模块
- 样式使用 CSS Variables 实现主题定制

## 相关链接

- [系统架构设计](../../artifacts/system-architecture.md)
- [任务拆分清单](../../artifacts/task-breakdown.md)
- [后端开发规范](../../code-guidelines/后端模块开发规范.md)
