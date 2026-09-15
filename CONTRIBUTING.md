# 贡献指南

> 感谢你愿意为「备忘录」出一份力。本文档说明**怎么提 Issue、怎么写代码、怎么提 PR**。
> 动手前请先读 [README.md](./README.md)（项目定位与技术栈）与 [architecture.md](./architecture.md)（具体实现方案）——两者是代码的最终依据，本文档只做流程与规范的补充。

---

## 目录

1. [可以贡献什么](#1-可以贡献什么)
2. [开发环境](#2-开发环境)
3. [分支与提交规范](#3-分支与提交规范)
4. [代码规范](#4-代码规范)
5. [数据库变更](#5-数据库变更)
6. [PR 流程](#6-pr-流程)
7. [Issue 规范](#7-issue-规范)
8. [行为准则](#8-行为准则)

---

## 1. 可以贡献什么

| 类型 | 说明 |
| --- | --- |
| 新功能 | 按 [architecture.md 第 7 节](./architecture.md#7-分阶段实施计划) 的阶段与优先级（P0 → P2）认领 |
| 修复缺陷 | 附上可复现步骤，最好带上复现截图或请求日志 |
| 文档 | 补漏、纠错、补示例；中英文表述统一用中文 |
| 重构 / 性能 | **必须先有 Issue 讨论并达成一致**，不接受无讨论的大范围重构 |
| 测试 | 补单元测试、接口测试 |

> **原则：先讨论，再动手。** 任何非琐碎的改动，请先开 Issue 说明「要解决什么问题、打算怎么做」，得到确认后再写代码，避免白做。

---

## 2. 开发环境

| 依赖 | 版本 | 说明 |
| --- | --- | --- |
| JDK | 17 | 后端编译运行 |
| Maven | 3.8+ | 后端构建（仓库自带 `mvnw`） |
| Node.js | 18+ | 前端构建 |
| pnpm | 8+ | 前端包管理器（**统一用 pnpm**，不要提交 `package-lock.json`） |
| MySQL | 8 | 本地数据库，库名 `memo`，字符集 `utf8mb4` |

### 启动步骤

```bash
# 1. 初始化数据库（建库建表）
mysql -uroot -p < docs/sql/schema.sql

# 2. 后端
cd backend
./mvnw spring-boot:run

# 3. 前端（另开终端）
cd frontend
pnpm install
pnpm dev
```

后端配置项（数据库密码、JWT 密钥）通过环境变量注入，**不要把本地密码写进 `application.yml` 后提交**：

```bash
export DB_PASSWORD=your_password
export JWT_SECRET=your_secret
```

---

## 3. 分支与提交规范

### 3.1 分支命名

```
<你的 GitHub 用户名>/<类型>--<简短描述>
```

示例：

```bash
git checkout -b a989464224/feat--笔记标签接口
git checkout -b zyongquan/fix--分享链接过期判断
git checkout -b someone/docs--补充部署说明
```

- 类型见 [3.2 提交类型表](#32-提交信息)。
- `main` 为保护分支，**任何人不得直接 push**，一律走 PR。

### 3.2 提交信息

格式（**注意用半角冒号 + 一个空格**）：

```
<type>: <做了什么>
```

| type | 含义 |
| --- | --- |
| `feat` | 新功能 |
| `fix` | 修复缺陷 |
| `docs` | 文档变更 |
| `refactor` | 重构（不改变外部行为） |
| `perf` | 性能优化 |
| `test` | 测试相关 |
| `style` | 格式调整（不影响逻辑） |
| `chore` | 构建、依赖、工具链 |

示例：

```
feat: 新增笔记标签的增删改查接口
fix: 修复删除笔记未级联清理 note_tag 的问题
docs: 补充分享接口的返回字段说明
```

补充要求：

- 描述用**中文**，一句话说清「做了什么」，不写「update」「修改一下」这类无信息量的内容。
- 一次提交只做一件事，不要把多个不相关的改动混在一个 commit 里。
- 关联 Issue 时在正文写 `Closes #12`。

---

## 4. 代码规范

[architecture.md 第 9 节「约定与规范」](./architecture.md#9-约定与规范) 是通用约定，以下为本项目**必须遵守**的硬性红线。

### 4.1 后端

| 规则 | 说明 |
| --- | --- |
| 单向依赖 | `Controller → Service → Mapper`，下层不得反向依赖上层 |
| 不暴露 entity | Controller 出入参一律用 `dto/` 下的 DTO / VO，**严禁直接接收或返回 entity** |
| 统一返回 | 所有接口返回 `Result<T>`，分页返回 `PageResult<T>`，不要自定义返回结构 |
| 异常处理 | 业务错误抛 `BusinessException`，由 `GlobalExceptionHandler` 统一兜底，**禁止在 Controller 里 try-catch 后手动拼错误** |
| 越权防护 | Service 中所有按 `id` 的操作，查询条件必须带 `user_id = 当前用户`，从 `UserContext` 取，**不允许信任前端传来的 userId** |
| 事务 | 涉及多表写操作（笔记 + 标签、级联删除等）必须加 `@Transactional` |
| 密码 | 一律 BCrypt 加密存储，日志和接口**绝不回传明文密码** |
| 参数校验 | 入参用 `@Valid` + JSR-303 注解校验，不在 Service 里手写一堆 if 判空 |

### 4.2 前端

| 规则 | 说明 |
| --- | --- |
| 接口调用 | 统一走 `src/api/` 封装，**组件内不直接写 axios 请求** |
| 状态管理 | 跨组件共享的状态放 Pinia store，组件内部状态用 `ref` / `reactive` |
| 类型 | 禁止滥用 `any`；接口出入参在 `src/types/` 定义类型，与后端 DTO/VO 一一对应 |
| XSS | Markdown 渲染**必须**做过滤（`markdown-it` 关闭 `html` 或经 `DOMPurify` 净化），不得直接 `v-html` 渲染用户原始输入 |
| 样式 | 颜色 / 字体等统一用 CSS 变量（`--bg`、`--fg`、`--accent`），不硬编码色值，保证主题可切换 |

### 4.3 命名

| 位置 | 规范 | 示例 |
| --- | --- | --- |
| Java 类 / 接口 | PascalCase | `NoteService` |
| Java 方法 / 变量 | camelCase | `listNotes` |
| 前端组件文件 | PascalCase | `NoteItem.vue` |
| 前端 TS 文件 | camelCase | `noteStore.ts` |
| SQL 表 / 字段 | snake_case | `note_tag`、`created_at` |

### 4.4 注释

- 注释写**为什么**，不写**做了什么**——代码本身已说明做什么。
- 公共方法、复杂业务分支、有坑的地方必须有注释；显而易见的 getter/setter 不用写。

---

## 5. 数据库变更

- **唯一建表来源**：`docs/sql/schema.sql`。任何表结构变更都必须同步修改该文件，**不允许只在本地改了库却不改脚本**。
- 变更表结构时，同时更新 [architecture.md 第 3 节](./architecture.md#3-数据库设计) 的建表 SQL 与表清单。
- 新增字段需带 `COMMENT` 注释；表名、字段名 `snake_case`。
- 涉及旧数据的变更，需在 PR 描述里说明**数据迁移方案**。

---

## 6. PR 流程

```bash
# 1. 同步最新 main
git checkout main && git pull

# 2. 开新分支
git checkout -b <用户名>/<type>--<描述>

# 3. 开发并提交（可多次提交）
git add <文件>
git commit -m "feat: xxx"

# 4. 推送前先同步 main，保持线性历史
git fetch origin && git rebase origin/main

# 5. 推送并到 GitHub 开 PR 到 main
git push -u origin <分支名>
```

### PR 描述必须包含

```markdown
## 做了什么
<!-- 一句话说明本次改动 -->

## 关联 Issue
Closes #12

## 怎么验证
<!-- 复现 / 验证步骤，最好附截图 -->

## 自查清单
- [ ] 接口返回统一 Result，未直接暴露 entity
- [ ] 按 id 的操作已带 user_id 条件
- [ ] 多表写操作已加 @Transactional
- [ ] 表结构变更已同步 docs/sql/schema.sql
- [ ] 无调试代码、console.log、注释掉的死代码
- [ ] 无本地密码 / 密钥等敏感信息提交
```

### Review 要求

- 至少 **1 名维护者 Approve** 后才能合并。
- Review 意见通过追加 commit 修改，**不要 force push 覆盖已有 review 记录**（rebase main 除外）。
- 合并方式由维护者选择；合并后请删除自己的分支。

---

## 7. Issue 规范

标题格式：

```
[类型] 简短描述
```

类型取 `Bug` / `Feature` / `Docs` / `Question`，例如：

```
[Bug] 编辑笔记时标签未保存
[Feature] 希望支持笔记导出为 Markdown 文件
```

内容要求：

| Issue 类型 | 必须包含 |
| --- | --- |
| Bug | 复现步骤、期望结果、实际结果、环境（浏览器 / JDK / MySQL 版本）、相关日志或截图 |
| Feature | 要解决什么问题（而不是「要加某功能」）、使用场景、可接受的替代方案 |
| Docs | 具体位置（文件 + 行号）、哪里不对、建议怎么改 |

- 提问前先搜一遍已有 Issue，避免重复。
- 报 Bug 请使用最新 `main` 分支复现，并确认不是本地环境配置问题。

---

## 8. 行为准则

- 尊重每一位贡献者，就事论事讨论技术方案，不做人身评价。
- 对新手友好：Review 时给出**具体改法和原因**，而不只是「这样写不对」。
- 不接受任何形式的广告、推广，以及提交来源不明、与项目无关的代码。
- 项目遵循「自托管、隐私优先」的理念：**不得引入任何形式的埋点、数据回传、广告 SDK**。

---

有疑问就在 Issue 里提出，或直接 @ 维护者。期待你的提交 🎉
