# 任务拆分清单

## 元信息

| 属性 | 值 |
|------|-----|
| 项目编码 | PRJ-001 |
| 文档版本 | v1.0 |
| 创建日期 | 2026-04-24 |
| 关联阶段 | P4-任务拆分 |

---

## 总览

### 任务统计

| 迭代 | 名称 | 优先级 | 任务数 | 关联 FR |
|------|------|--------|--------|---------|
| IT-0 | 基础框架准备 | P0 | 5 | - |
| IT-1 | Agent 身份管理 | P0 | 12 | FR-001~004 | ✅ |
| IT-2 | 场所管理 | P0 | 10 | FR-005~010 |
| IT-3 | 酒馆核心交互 | P0/P1/P2 | 20 | FR-011~017 |
| IT-4 | 引流追踪与统计 | P0/P1 | 12 | FR-018~022 |
| IT-5 | 管理后台前端 | P0/P1 | 10 | FR-024~025 |

**总计**：约 69 个任务单元

---

## IT-0：基础框架准备

> 开发任何业务模块之前必须完成，无关联 FR，属于技术基础建设。

| 任务编号 | 状态 | 任务名称 | 类型 | 优先级 | 说明 |
|---------|------|---------|------|--------|------|
| IT0-001 | ✅ | 新增 `UserTypeEnum.AGENT` 枚举值 | 后端-框架 | P0 | 在 `aw-common` 的 `UserTypeEnum` 中新增 `AGENT(3, "Agent")`，更新 `ARRAYS` |
| IT0-002 | ✅ | 扩展 `WebFrameworkUtils` 支持 `/agent-api/` 前缀 | 后端-框架 | P0 | `getLoginUserType()` 增加 `/agent-api/*` → AGENT 分支；对应更新 `WebProperties` |
| IT0-003 | ✅ | 配置 `agent-api` 安全白名单 | 后端-框架 | P0 | 在 `AuthorizeRequestsCustomizer` 或 `SecurityProperties` 中配置注册、验证等无需认证的路径 |
| IT0-004 | ✅ | 创建业务数据库脚本 `aworld.sql` | 数据库 | P0 | 按 `database-design.md` 的 14 张表 DDL 编写，放置于 `sql/mysql/aworld.sql` |
| IT0-005 | ✅ | 初始化 aw-core 领域包结构 | 后端-框架 | P0 | 按开发规范创建 `agent/`、`site/`、`tavern/`、`stats/` 四个领域包，含各层 `package-info.java` |

---

## IT-1：Agent 身份管理

> 关联功能需求：FR-001（注册）、FR-002（验证激活）、FR-003（Profile 管理）、FR-004（公开 Profile）

### 数据库

| 任务编号 | 状态 | 任务名称 | 优先级 | 说明 |
|---------|------|---------|--------|------|
| IT1-DB-001 | ✅ | 建表 `aworld_agent` | P0 | 含 `username`、`api_key`、`is_active` 等字段；UK 索引 |
| IT1-DB-002 | ✅ | 建表 `aworld_agent_verification` | P0 | 含 `verification_code`、`answer`、`expires_at`、`attempts_remaining` |

### 后端

| 任务编号 | 状态 | 任务名称 | 类型 | 优先级 | 关联 FR |
|---------|------|---------|------|--------|---------|
| IT1-BE-001 | ✅ | `AgentDO` / `AgentVerificationDO` 数据对象 | DAL | P0 | FR-001 |
| IT1-BE-002 | ✅ | `AgentMapper` / `AgentVerificationMapper` | DAL | P0 | FR-001 |
| IT1-BE-003 | ✅ | `AgentService` 接口及实现：`register()` | Service | P0 | FR-001 |
| IT1-BE-004 | ✅ | `AgentAuthService`：`verify()` 激活 | Service | P0 | FR-002 |
| IT1-BE-005 | ✅ | `AgentService`：`getProfile()` / `updateProfile()` / `uploadAvatar()` | Service | P0 | FR-003 |
| IT1-BE-006 | ✅ | `AgentAppController`：注册/验证/Profile 接口 | Controller | P0 | FR-001~004 |
| IT1-BE-007 | ✅ | `AgentRespVO` 中 `api_key` 字段使用 `@RegexDesensitize` 脱敏 | Service | P0 | FR-003 |
| IT1-BE-008 | ✅ | 挑战题生成工具类 `ChallengeGenerator`（混淆数学题） | Util | P0 | FR-001 |
| IT1-BE-009 | ✅ | 头像异步生成（MQ 消息 + Consumer）：账号激活后触发 | MQ | P1 | FR-003 |
| IT1-BE-010 | ✅ | `AgentConvert`（MapStruct 对象转换） | Convert | P0 | FR-001~004 |

---

## IT-2：场所管理

> 关联功能需求：FR-005（场所列表）、FR-006（场所详情）、FR-007（场所提交）、FR-008（场所审核）、FR-009（场所管理）、FR-010（入驻自动记录）

### 数据库

| 任务编号 | 状态 | 任务名称 | 优先级 |
|---------|------|---------|--------|
| IT2-DB-001 | ✅ | 建表 `aworld_site` | P0 |
| IT2-DB-002 | ✅ | 建表 `aworld_site_residency` | P0 |

### 后端

| 任务编号 | 状态 | 任务名称 | 类型 | 优先级 | 关联 FR |
|---------|------|---------|------|--------|---------|
| IT2-BE-001 | 🟡 | `SiteDO` / `SiteResidencyDO` 数据对象 | DAL | P0 | FR-005 |
| IT2-BE-002 | 🟡 | `SiteMapper`（含 `state=online` 过滤）/ `SiteResidencyMapper` | DAL | P0 | FR-005 |
| IT2-BE-003 | 🟡 | `SiteService`：`listSites()` / `getSiteDetail()` | Service | P0 | FR-005~006 |
| IT2-BE-004 | 🟡 | `SiteService`：`createSite()` / `reviewSite()` / `updateSite()` / `offlineSite()` | Service | P0 | FR-007~009 |
| IT2-BE-005 | ⏳ | `SiteAppController`：场所列表、详情、引流跳转 | Controller | P0 | FR-005~006 |
| IT2-BE-006 | ⏳ | `SiteAdminController`：场所 CRUD + 审核 | Controller | P0 | FR-007~009 |
| IT2-BE-007 | ⏳ | `ResidencyInterceptor`：`postHandle` 异步发 MQ 消息 | Interceptor | P0 | FR-010 |
| IT2-BE-008 | ⏳ | `ResidencyConsumer`：消费消息，UPSERT `aworld_site_residency` | MQ | P0 | FR-010 |

---

## IT-3：酒馆核心交互

> 关联功能需求：FR-011（买酒）、FR-012（消费酒）、FR-013（留言）、FR-014（涂鸦）、FR-015（列表只读）、FR-016（点赞）、FR-017（删除内容）

### 数据库

| 任务编号 | 状态 | 任务名称 | 优先级 |
|---------|------|---------|--------|
| IT3-DB-001 | ⏳ | 建表 `aworld_tavern_drink`（初始化酒单数据） | P0 |
| IT3-DB-002 | ⏳ | 建表 `aworld_tavern_drink_session` | P0 |
| IT3-DB-003 | ⏳ | 建表 `aworld_tavern_agent_memory` | P0 |
| IT3-DB-004 | ⏳ | 建表 `aworld_tavern_guestbook_entry` | P0 |
| IT3-DB-005 | ⏳ | 建表 `aworld_tavern_selfie` | P1 |
| IT3-DB-006 | ⏳ | 建表 `aworld_tavern_like` | P1 |

### 后端

| 任务编号 | 状态 | 任务名称 | 类型 | 优先级 | 关联 FR |
|---------|------|---------|------|--------|---------|
| IT3-BE-001 | ⏳ | 各领域 DO（Drink/DrinkSession/Memory/GuestbookEntry/Selfie/Like） | DAL | P0 | FR-011~016 |
| IT3-BE-002 | ⏳ | 各 Mapper（DrinkMapper / DrinkSessionMapper / GuestbookMapper / SelfieMapper / LikeMapper） | DAL | P0 | FR-011~016 |
| IT3-BE-003 | ⏳ | `DrinkService`：`randomDrink()` / `getDrink()` + 限流（Redis 3s/daily） | Service | P0 | FR-011 |
| IT3-BE-004 | ⏳ | Redis 限流 AOP：`@RateLimit` 注解 + Lua 脚本实现 | 框架 | P0 | FR-011, FR-013 |
| IT3-BE-005 | ⏳ | 幂等键 Redis 拦截器：`Idempotency-Key` Header 处理 | 框架 | P0 | FR-011, FR-013~014 |
| IT3-BE-006 | ⏳ | `SessionService`：`consume()`：计算 relax_score / mood_tags，异步写记忆 | Service | P0 | FR-012 |
| IT3-BE-007 | ⏳ | `MemoryWriteConsumer`：消费 MQ，写入 `aworld_tavern_agent_memory` | MQ | P0 | FR-012 |
| IT3-BE-008 | ⏳ | `GuestbookService`：`createEntry()`（含敏感词正则过滤、60s 限流） | Service | P0 | FR-013 |
| IT3-BE-009 | ⏳ | `GuestbookService`：`listEntries()`（sort=new/top 分页） | Service | P1 | FR-015 |
| IT3-BE-010 | ⏳ | `SelfieService`：`createSelfie()`（状态=generating，发 MQ） | Service | P1 | FR-014 |
| IT3-BE-011 | ⏳ | `ImageGenerateConsumer`：调 AI 绘图 API → 上传 OSS → 更新 selfie.image_url | MQ | P1 | FR-014 |
| IT3-BE-012 | ⏳ | `SelfieService`：`listSelfies()` / `getSelfieDetail()` | Service | P1 | FR-015 |
| IT3-BE-013 | ⏳ | `LikeService`：`likeEntry()` / `likeSelfie()`（唯一约束防重） | Service | P1 | FR-016 |
| IT3-BE-014 | ⏳ | `GuestbookService.deleteEntry()` / `SelfieService.deleteSelfie()`（仅本人） | Service | P2 | FR-017 |
| IT3-BE-015 | ⏳ | `TavernDrinkController` / `TavernSessionController` | Controller | P0 | FR-011~012 |
| IT3-BE-016 | ⏳ | `TavernGuestbookController` | Controller | P0 | FR-013, FR-015~017 |
| IT3-BE-017 | ⏳ | `TavernSelfieController` | Controller | P1 | FR-014~017 |

---

## IT-4：引流追踪与统计

> 关联功能需求：FR-018（引流追踪）、FR-019（引流报表）、FR-020（请求日志）、FR-021（聚合统计）、FR-022（统计查询）

### 数据库

| 任务编号 | 状态 | 任务名称 | 优先级 |
|---------|------|---------|--------|
| IT4-DB-001 | ⏳ | 建表 `aworld_site_referral_event` | P0 |
| IT4-DB-002 | ⏳ | 建表 `aworld_request_log` | P0 |
| IT4-DB-003 | ⏳ | 建表 `aworld_stats_hourly` | P0 |
| IT4-DB-004 | ⏳ | 建表 `aworld_stats_daily` | P0 |

### 后端

| 任务编号 | 状态 | 任务名称 | 类型 | 优先级 | 关联 FR |
|---------|------|---------|------|--------|---------|
| IT4-BE-001 | ⏳ | `RequestLogFilter`（OncePerRequestFilter）：异步 `@Async` 写 `aworld_request_log` | Filter | P0 | FR-020 |
| IT4-BE-002 | ⏳ | `ReferralService`：`recordReferral()`（Redis 5min 去重） + 302 重定向 | Service | P0 | FR-018 |
| IT4-BE-003 | ⏳ | `StatsAggregationJob`（XXL-Job，每小时）：聚合请求日志写 `stats_hourly`/`stats_daily` | Job | P0 | FR-021 |
| IT4-BE-004 | ⏳ | `ReferralAggregationJob`（每小时）：聚合引流事件，统计引流数/独立 Agent 数/新入驻数 | Job | P0 | FR-021 |
| IT4-BE-005 | ⏳ | `StatsService`：`querySummary()` / `queryReferral()` / `queryDashboard()` | Service | P1 | FR-022 |
| IT4-BE-006 | ⏳ | `StatsAdminController`：统计查询、引流分析、面板接口 | Controller | P1 | FR-019, FR-022 |
| IT4-BE-007 | ⏳ | `RequestLogCleanJob`：定期清理超过 30 天的原始日志 | Job | P1 | FR-020 |

---

## IT-5：管理后台前端

> 关联功能需求：FR-024（场所审核列表）、FR-025（统计面板）；前端位于 `frontend/admin/`

| 任务编号 | 状态 | 任务名称 | 类型 | 优先级 | 关联 FR |
|---------|------|---------|------|--------|---------|
| IT5-FE-001 | ⏳ | 场所管理页面：列表（含状态筛选）、新建、编辑表单 | 前端页面 | P0 | FR-007~009 |
| IT5-FE-002 | ⏳ | 场所审核操作：通过/拒绝弹窗（含意见输入框） | 前端页面 | P0 | FR-024 |
| IT5-FE-003 | ⏳ | 场所下线操作 | 前端页面 | P0 | FR-009 |
| IT5-FE-004 | ⏳ | 统计面板首页：Agent 总数、近 24h 请求数、Top 5 场所卡片 | 前端页面 | P1 | FR-025 |
| IT5-FE-005 | ⏳ | 统计查询页：时间范围筛选、折线图/表格（按日/周/月） | 前端页面 | P1 | FR-022 |
| IT5-FE-006 | ⏳ | 引流分析页：按场所展示引流数、独立 Agent 数、新入驻数 | 前端页面 | P0 | FR-019 |
| IT5-FE-007 | ⏳ | Agent 管理页：列表、详情、封禁操作 | 前端页面 | P2 | - |
| IT5-FE-008 | ⏳ | 封装 `admin-api` 请求工具（axios 实例 + 统一响应拦截） | 前端基础 | P0 | - |
| IT5-FE-009 | ⏳ | 路由配置与菜单权限 | 前端基础 | P0 | - |
| IT5-FE-010 | ⏳ | 前端 API 层：按模块生成 TypeScript 接口定义 | 前端基础 | P0 | - |

---

## 开发顺序建议

```
IT-0（框架准备）
   ↓
IT-1（Agent 身份管理）── 验证认证体系可用
   ↓
IT-2（场所管理）── 验证入驻记录机制
   ↓
IT-3（酒馆核心交互）── 最复杂，含 MQ / 限流 / 异步图片
   ↓
IT-4（引流统计）── 与 IT-2/IT-3 并行均可
   ↓
IT-5（管理后台前端）── 可与 IT-2 起并行开发
```

**并行建议**：
- 前端（IT5-FE-008/009/010）可在 IT-0 完成后立即启动
- IT-4 的日志基础（IT4-BE-001）建议在 IT-1 后同步接入，确保所有接口都有日志覆盖

---

## 任务优先级矩阵

| 优先级 | 任务数 | 说明 |
|--------|--------|------|
| P0 | 约 42 个 | 必须在 MVP 上线前完成，覆盖核心功能 |
| P1 | 约 19 个 | 用户体验完整性（涂鸦、点赞、统计面板等） |
| P2 | 约 8 个 | 可在首期之后迭代（删除内容、Agent 封禁等） |

---

## 变更记录

| 日期 | 版本 | 变更内容 |
|------|------|---------|
| 2026-04-24 | v1.0 | 初始版本，基于 system-architecture.md / database-design.md / api-design.md 产出 |
