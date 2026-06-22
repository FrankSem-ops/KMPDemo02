# 实施计划：[FEATURE]

**分支**：`[###-feature-name]` | **日期**： [DATE] | **规格**： [link]

**输入**：来自 `/specs/[###-feature-name]/spec.md` 的功能规格

**说明**：此模板由 `/speckit-plan` 命令填充。执行流程说明见 `.specify/templates/plan-template.md`。

## 摘要

[从功能规格中提炼：核心需求 + 研究得出的技术方案]

## 技术上下文

<!--
  需要操作：请用当前项目的真实技术细节替换本节内容。
  这里的结构只是为了帮助你整理信息和推进迭代。
-->

**语言/版本**： [例如：Python 3.11、Swift 5.9、Rust 1.75，或 NEEDS CLARIFICATION]

**主要依赖**： [例如：FastAPI、UIKit、LLVM，或 NEEDS CLARIFICATION]

**存储**： [如适用，例如：PostgreSQL、CoreData、文件，或 N/A]

**测试**： [例如：pytest、XCTest、cargo test，或 NEEDS CLARIFICATION]

**目标平台**： [例如：Linux server、iOS 15+、WASM，或 NEEDS CLARIFICATION]

**项目类型**： [例如：library/cli/web-service/mobile-app/compiler/desktop-app，或 NEEDS CLARIFICATION]

**性能目标**： [领域相关，例如：1000 req/s、10k lines/sec、60 fps，或 NEEDS CLARIFICATION]

**约束条件**： [领域相关，例如：<200ms p95、<100MB memory、支持离线，或 NEEDS CLARIFICATION]

**规模/范围**： [领域相关，例如：10k users、1M LOC、50 screens，或 NEEDS CLARIFICATION]

## 宪章检查

*门禁：必须在 Phase 0 研究前通过，并在 Phase 1 设计后再次检查。*

[根据宪章文件得出的检查门禁]

## 项目结构

### 文档产物（本功能）

```text
specs/[###-feature]/
├── plan.md              # 本文件（/speckit-plan 命令输出）
├── research.md          # Phase 0 输出（/speckit-plan 命令）
├── data-model.md        # Phase 1 输出（/speckit-plan 命令）
├── quickstart.md        # Phase 1 输出（/speckit-plan 命令）
├── contracts/           # Phase 1 输出（/speckit-plan 命令）
└── tasks.md             # Phase 2 输出（/speckit-tasks 命令生成，不由 /speckit-plan 创建）
```

### 源码结构（仓库根目录）
<!--
  需要操作：请把下面的占位目录树替换成这个功能实际对应的项目结构。
  删除未使用的选项，并将选中的结构展开成真实路径。
  最终交付的计划里不要保留 “Option” 之类的标签。
-->

```text
# [未使用请删除] 方案 1：单项目（默认）
src/
├── models/
├── services/
├── cli/
└── lib/

tests/
├── contract/
├── integration/
└── unit/

# [未使用请删除] 方案 2：Web 应用（检测到 frontend + backend 时）
backend/
├── src/
│   ├── models/
│   ├── services/
│   └── api/
└── tests/

frontend/
├── src/
│   ├── components/
│   ├── pages/
│   └── services/
└── tests/

# [未使用请删除] 方案 3：移动端 + API（检测到 iOS/Android 时）
api/
└── [同上面的 backend 结构]

ios/ 或 android/
└── [平台特定结构：功能模块、UI 流程、平台测试]
```

**结构决策**： [说明最终采用的结构，并引用上面记录的真实目录]

## 复杂度跟踪

> **只有当宪章检查出现必须说明理由的违规项时，才填写本节**

| 违规项 | 为什么需要 | 为什么更简单的方案不行 |
|--------|------------|------------------------|
| [例如：第 4 个子项目] | [当前需求] | [为什么 3 个子项目不够] |
| [例如：Repository 模式] | [具体问题] | [为什么直接访问数据库不够] |
