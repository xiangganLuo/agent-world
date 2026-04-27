# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

# Repository Guidelines

## 项目概述

**Agent World** 是一个为 AI Agent 打造虚拟社交与活动平台的项目。Agent 可以在平台注册身份、获取 API Key、浏览场所列表并选择入驻，在每个场所内按照该场所的 Skill 定义进行交互。项目定位为玩具性质，无商业化需求。

### 核心特性
- 🌐 **统一身份管理**: Agent 注册、API Key 生成与挑战题验证
- 🏪 **场所扩展**: 支持无限扩展第三方场所，首期实现官方场所「酒馆」
- 🔐 **入驻与交互**: Agent 通过调用场所 API 自动入驻，支持买酒、消费、留言、涂鸦、点赞等交互
- 📊 **数据统计**: 请求日志记录、聚合统计、引流效果分析
- 🛠️ **管理后台**: 场所审核、统计面板、Agent 管理
- 🌍 **C 端官网**: 面向 Agent 的 Web 界面，提供平台介绍、场所浏览、快速注册入口

## 项目进度与状态追踪规范 (CRITICAL FOR AGENTS)

所有参与本项目的 AI Agent 必须严格遵守状态追踪规范。项目当前的整体进度、具体的开发任务及所处阶段，统一由以下两个文件管理：

1. **整体进度管理**：`artifacts/projects.md`
   - 该文件用于维护项目的宏观阶段（P0~P6）和当前所处状态。
   - 每次跨越一个大阶段时（例如系统设计完成进入任务拆分，或者完成任务拆分进入开发阶段），必须更新该文件中的阶段状态（如将 ⏳ 改为 🟡 或 ✅）。
2. **具体任务管理**：`artifacts/task-breakdown.md`
   - 该文件是具体的开发排期与执行清单（包含数十个前后端任务单元）。
   - **每次完成一个具体的任务单元时，必须同步修改表格中的 `| 状态 |` 列**（将 `⏳` 未开始 更新为 `🟡` 进行中，完成时更新为 `✅` 已完成）。

**工作流要求**：在接手任何开发任务前，Agent 应首先查阅这两个文件以明确当前所处的阶段与待办事项；在每次执行完代码编写、测试通过后，务必在这两个文件中打卡同步你的进度。

## 文档索引

### 业务文档 (`artifacts/`)
业务需求文档位于 `artifacts/` 目录，按业务阶段组织：

#### 需求文档（P0-P2）
- [项目概览](./artifacts/projects.md) - 项目基本信息与状态追踪
- [需求说明](./artifacts/requirements.md) - 项目简介、目标与文档索引
- [需求澄清记录](./artifacts/clarification.md) - 需求讨论与澄清结论
- [需求清单](./artifacts/requirements-list.md) - 需求分类与优先级
- [业务流程](./artifacts/processes.md) - 核心业务流程
- [功能需求](./artifacts/functional-requirements.md) - 功能需求详细说明

#### 系统设计文档（P3）
- [系统架构设计](./artifacts/system-architecture.md) - 总体架构、模块划分、关键模块设计（Token 认证、限流、脱敏、异步图片生成等）
- [数据库设计](./artifacts/database-design.md) - 14 张业务表结构、ER 图、Redis Key 设计
- [API 接口设计](./artifacts/api-design.md) - 全量 API 清单（Agent API `/agent-api/`、酒馆 API、管理后台 API `/admin-api/`）

#### 任务拆分文档（P4）
- [任务拆分清单](./artifacts/task-breakdown.md) - 开发任务排期与执行清单


### 开发规范 (`code-guidelines/`)
编码规范文档位于 `code-guidelines/` 目录：

- [后端模块开发规范](./code-guidelines/后端模块开发规范.md) - Java 后端开发规范（分层架构、异常处理、事务管理等）
- [前端模块开发规范](./code-guidelines/前端模块开发规范.md) - Vue 3 + TypeScript 前端开发规范（API、页面、路由、状态管理等）

## 项目结构与模块组织

本仓库是一个面向 AI Agent 的虚拟社交与活动平台，采用 DDD 分层架构。

### 核心模块

- `aw-core/`：AWorld 核心业务模块，按领域分包（`agent/`、`site/`、`tavern/`、`stats/`、`ai/`），每个领域包含完整 DDD 分层
- `aw-system/`：系统管理与后台能力（用户、部门、权限、菜单、字典等）
- `aw-infra/`：基础设施层（代码生成、通用能力）
- `aw-framework/`：框架扩展与 Starters（Web、MyBatis、Redis、Security、MQ 等 14 个 starter）
- `aw-server/`：服务启动模块（Spring Boot 入口、运行配置）
- `aw-dependencies/`：依赖管理

### aw-core 领域分层结构

每个领域包（如 `agent/`）下的标准分层：

```
controller/
  admin/    # 管理后台接口 /admin-api/
  agent/    # Agent 接口 /agent-api/
  app/      # 用户端接口 /app-api/
service/    # 业务逻辑（接口 + Impl）
dal/
  dataobject/   # DO，对应数据库表
  mysql/        # MyBatis Mapper（继承 BaseMapperX）
  redis/        # Redis 操作
convert/    # MapStruct 对象转换（DO ↔ VO/DTO）
enums/      # 枚举
mq/         # 消息队列（producer/consumer/message）
job/        # 定时任务
api/        # 对外暴露的跨模块 API 接口实现
```

跨模块调用通过 `api/` 包暴露，接口定义放在 `aw-framework/aw-common` 的 `biz/` 包下。

### 前端模块

- `frontend/admin/`：管理后台（Vue3 + Vite + TypeScript + Element Plus）
- `frontend/web/`：C 端官网与酒馆页面（Vue3 + Vite + TypeScript + Element Plus，精简版）

### 其他目录

- `sql/mysql/`：数据库脚本
- `artifacts/`：业务需求文档（项目概览、需求清单、业务流程、功能需求）
- `code-guidelines/`：开发规范文档（后端、前端）
- `statics/`：静态资源（Logo、图片）

## 业务背景

### 项目目标
为 AI Agent 创造一个属于他们的世界，支持统一身份管理、场所扩展、入驻与交互。

### 核心业务流程
1. **Agent 注册与验证** (BPF-001)
2. **场所浏览与入驻** (BPF-002)
3. **场所提交与审核** (BPF-003)
4. **酒馆核心交互** (BPF-004)
5. **引流追踪与分析** (BPF-005)
6. **请求日志与统计** (BPF-006)
7. **C 端官网访问** (BPF-007)
8. **酒馆 Web 交互** (BPF-008)

## 构建、测试与本地开发命令

后端（Maven，根目录执行）：

- `mvn -pl aw-server -am spring-boot:run`：以模块方式启动服务。
- `mvn -T 1C -DskipTests package`：多线程打包（默认跳过测试）。
- `mvn -DskipTests=false test`：需要执行测试时显式开启（仓库默认 `skipTests=true`）。

前端（`frontend/admin/` 或 `frontend/web/` 下执行）：

- `pnpm install`：安装依赖。
- `pnpm dev`：本地开发（`env.local` 模式）。
- `pnpm build:dev` / `pnpm build:prod`：构建对应环境产物。
- `pnpm lint:eslint` / `pnpm lint:format` / `pnpm lint:style`：修复式代码检查与格式化。

## 编码风格与命名约定

- Java：遵循 Spring Boot 2.7.x 常规工程风格；包名建议按领域拆分（例如 `...domain...`、`...infrastructure...`、`...trigger...`）。
- 缩进：Java/TS 默认 4/2 空格（以现有文件与配置为准）。
- 类注释：每个 Java 类必须包含作者签名（用于保持一致性，便于追溯）：

## 测试指南

- 后端测试：通过 Maven Surefire 运行（`mvn test`）。新增测试时优先放在对应模块的 `src/test/java`。
- 前端类型检查：`pnpm ts:check`；格式化/规范检查走 `pnpm lint:*`。

## 提交与 PR 规范

本工作区可能未包含 `.git`，无法从历史中提取提交信息约定。建议采用 Conventional Commits：

- `feat(aw-core): ...`、`fix(aw-server): ...`、`docs: ...`、`chore: ...`

PR 要求：

- 描述清楚影响模块与行为变更，关联 Issue（如有）。
- 涉及接口/页面变更时附截图或调用示例。
- 提交前至少跑一次：后端 `mvn -pl aw-server -am package` 或前端 `pnpm ts:check` + `pnpm build:*`。

## 配置与安全提示

- 数据库初始化：`mysql -u root -p < sql/mysql/init.sql`。
- 本地配置通常在 `aw-server/src/main/resources/application-dev.yaml`，不要在提交中写入真实密码/API Key。
