# 备忘录

> 一个**自托管、轻量极简**的备忘录/笔记应用。用 Java + Vue 重新实现。

## 设计理念

1. **自托管、隐私优先**：数据完全掌控在自己手里，无追踪、无广告、无订阅费。
2. **开源、永久免费**：代码开源透明，不绑定任何厂商。
3. **轻量、极简**：纯文本 + Markdown，拒绝复杂富文本的心智负担，界面干净。
4. **快速部署、低资源占用**：一条命令即可跑起来，资源占用小。
5. **可定制、可分享**：支持自定义主题，笔记可对外分享、协作。
6. **API 优先**：提供完整 REST 接口，方便第三方工具与自动化脚本集成。

## 技术栈

| 层 | 技术 | 说明 |
| --- | --- | --- |
| 前端 | Vue 3 + TypeScript + Vite | 纯前端，通过 REST 调用后端 |
| 前端状态/路由 | Pinia + Vue Router | 状态管理与路由 |
| UI | Element Plus | 组件库 |
| 后端 | Java 17 + Spring Boot 3 | REST 接口服务 |
| 数据访问 | MyBatis-Plus | ORM 数据访问 |
| 数据库 | MySQL 8 | 生产数据库 |

## 架构

```
┌─────────────────────────────────────────────┐
│                 前端 (Vue 3)                 │  纯前端,调 REST API
└──────────────────────┬──────────────────────┘
                       │  REST (JSON)
┌──────────────────────▼──────────────────────┐
│               Controller 层                  │  接收请求、参数校验、返回统一结果
├─────────────────────────────────────────────┤
│               Service 层                     │  业务逻辑
├─────────────────────────────────────────────┤
│               Mapper 层                      │  数据访问(MyBatis-Plus)
└──────────────────────┬──────────────────────┘
                       │
                  ┌────▼────┐
                  │  MySQL  │
                  └─────────┘
```

### 核心原则

1. **单向依赖**：Controller → Service → Mapper，上层调下层，下层不反向依赖。
2. **分层职责清晰**：Controller 只管收发，Service 管业务，Mapper 管 SQL，互不越界。
3. **前后端分离**：前端只通过 API 通信，不碰数据库。

## 目录结构

```
backend/                          # Java 后端 (Spring Boot)
├── pom.xml
└── src/main/
    ├── java/com/aireview/
    │   ├── AiReviewApplication.java   # 启动类
    │   ├── controller/                # 接口层：收发请求、参数校验
    │   ├── service/                   # 业务层：核心业务逻辑
    │   │   └── impl/                  # 业务实现
    │   ├── mapper/                    # 数据层：MyBatis-Plus Mapper
    │   ├── entity/                    # 数据库实体
    │   ├── dto/                       # 出入参对象 (DTO/VO)
    │   ├── config/                    # 配置 (MyBatis、CORS、拦截器)
    │   └── common/                    # 公共 (统一返回 Result、全局异常)
    └── resources/
        ├── application.yml            # 配置文件
        └── mapper/                    # MyBatis XML 映射
frontend/                         # 前端 (Vue 3 + TS)
├── package.json
├── vite.config.ts
└── src/
    ├── api/                           # 接口封装 (axios)
    ├── views/                         # 页面
    ├── components/                    # 通用组件
    ├── stores/                        # Pinia 状态
    ├── router/                        # Vue Router 路由
    ├── types/                         # TS 类型定义
    └── utils/                         # 工具函数
docs/                             # 文档、SQL 脚本
```

## 快速开始

### 后端

```bash
cd backend
./mvnw spring-boot:run
```

### 前端

```bash
cd frontend
pnpm install
pnpm dev
```

## 更多文档

- [architecture.md](./architecture.md) — 项目架构设计说明
- [CONTRIBUTING.md](./CONTRIBUTING.md) — 贡献指南（分支/提交规范、代码红线、PR 流程）
