# FoodChin 可信评价数据模型设计

## 1. 目标与边界

FoodChin 采用混合架构：MariaDB 保存可检索、可统计、可供机器学习实验复现的业务投影；FISCO BCOS 保存需要被独立核验的食品追溯证据与评价存证。MariaDB 不是链上事实的替代品，链上记录及其交易回执才是可信证明来源。

本设计只定义数据模型和后续实施规则，不执行 DDL，也不改变现有合约、接口或业务流程。

## 2. 食品身份与 ID 映射

现有合约以 `traceNumber`（`uint256`）作为食品的业务标识；它已经被现有前端和食品、追溯接口使用。因此，`traceNumber` 必须保持不变，不能由数据库主键取代。

推荐采用“双标识”模型：

| 标识 | 归属 | 用途 | 约束 |
| --- | --- | --- | --- |
| `food_id` | MariaDB | 本地关联键，连接评价、特征和本地查询投影 | `BIGINT`，内部主键 |
| `trace_number` | FISCO BCOS / 业务域 | 食品跨系统业务标识、链上查询参数 | `VARCHAR(78)`，全局唯一；保存十进制字符串以兼容 `uint256` |
| `transaction_hash` | FISCO BCOS | 一次链上交易的不可篡改凭证 | `VARCHAR(128)`，在网络内唯一 |

在第一阶段应建立一个**食品身份投影**（可命名为 `food` 或 `food_identity`）。它至少包含 `food_id`、`trace_number`、`created_time`，并对 `trace_number` 建立唯一索引。由于当前 MariaDB 没有食品表，该投影是 Evaluation 表落库前的必要前置条件；不建议让 `food_evaluation.food_id` 直接保存字符串 `traceNumber`。

食品创建完成并取得链上交易哈希后，系统以 `traceNumber` 查询或确认链上状态，再创建/更新本地身份投影。一个食品身份对应多个评价；一条链上交易可服务于食品创建、追溯更新或评价存证等不同业务事件。

## 3. 表设计

### 3.1 前置食品身份投影

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `id` | BIGINT | 本地 `food_id` 主键 |
| `trace_number` | VARCHAR(78) | 链上 `uint256` 的十进制字符串，唯一 |
| `created_time` | DATETIME | 本地投影创建时间 |

该表是最小身份映射。后续若 Food 模块落库，可扩展食品名称、类别等字段，或合并到正式 `food` 表；`id` 和 `trace_number` 的语义不得改变。

### 3.2 `food_evaluation`

一行表示一次可复现的食品质量评价。

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `id` | BIGINT | 评价主键 |
| `food_id` | BIGINT | 关联食品身份投影 |
| `score` | DECIMAL(5,2) | 0–100 的评分 |
| `level` | VARCHAR(16) | A/B/C/D 等质量等级 |
| `confidence` | DECIMAL(5,4) | 模型置信度，可空 |
| `model_id` | BIGINT | 关联 `model_info`；模型未纳管时可空 |
| `model_version` | VARCHAR(64) | 冗余保存版本，保证历史可读性 |
| `evaluation_time` | DATETIME | 评分业务发生时间 |
| `chain_status` | VARCHAR(32) | `PENDING`、`SUBMITTED`、`CONFIRMED`、`FAILED`、`RECONCILING` |
| `created_time` | DATETIME | 本地创建时间 |

索引建议：`(food_id, evaluation_time)`、`chain_status`、`model_id`。原候选脚本中的 `food_id VARCHAR(64)` 应在实施前调整为该本地外键类型。

### 3.3 `evaluation_history`

记录评价状态与补偿过程，满足审计和论文复现实验需求。

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `id` | BIGINT | 主键 |
| `evaluation_id` | BIGINT | 关联评价 |
| `operation` | VARCHAR(64) | 如 `CREATED`、`CHAIN_SUBMITTED`、`CHAIN_CONFIRMED`、`RETRY_SCHEDULED` |
| `description` | VARCHAR(500) | 非敏感事件说明或错误摘要 |
| `created_time` | DATETIME | 事件时间 |

索引建议：`(evaluation_id, created_time)`。

### 3.4 `feature_snapshot`

保存每次评价实际使用的输入特征，不能只按食品保存，否则同一食品的不同评价无法复现。

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `id` | BIGINT | 主键 |
| `evaluation_id` | BIGINT | 关联本次评价 |
| `food_id` | BIGINT | 冗余关联，便于按食品检索 |
| `feature_name` | VARCHAR(128) | 特征名，例如运输温度 |
| `feature_value` | VARCHAR(512) | 规范化后的值；数值使用十进制文本 |
| `source` | VARCHAR(128) | 数据来源，如链上追溯、检测记录、人工录入 |
| `created_time` | DATETIME | 快照时间 |

唯一性建议：`(evaluation_id, feature_name)`；索引建议：`(food_id, created_time)`。原候选脚本应增加 `evaluation_id`。

### 3.5 `model_info`

记录训练后可用于评分的模型版本及实验指标。

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `id` | BIGINT | 主键 |
| `model_name` | VARCHAR(128) | 模型名称 |
| `version` | VARCHAR(64) | 不可变版本号 |
| `algorithm` | VARCHAR(64) | Random Forest、XGBoost 等 |
| `accuracy` | DECIMAL(6,5) | 准确率，可空 |
| `metrics_json` | TEXT | Precision、Recall、F1、数据集标识等扩展指标 |
| `created_time` | DATETIME | 登记时间 |

保留唯一约束 `(model_name, version)`。模型文件或训练集不直接写入本表，应保存其版本、摘要和受控存储位置。

### 3.6 `blockchain_transaction`

记录本地业务对象与链上交易之间的对应关系；它是链数据库同步和失败补偿的核心。

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `id` | BIGINT | 主键 |
| `business_type` | VARCHAR(64) | `FOOD_CREATE`、`TRACE_UPDATE`、`EVALUATION_ANCHOR` 等 |
| `business_id` | BIGINT | 对应本地业务主键；评价存证时为 `food_evaluation.id` |
| `food_id` | BIGINT | 可空，便于按食品追踪 |
| `transaction_hash` | VARCHAR(128) | WeBASE/FISCO 返回的交易哈希，提交前可空 |
| `contract_address` | VARCHAR(128) | 实际调用合约地址 |
| `block_number` | BIGINT | 回执确认后的区块高度，可空 |
| `status` | VARCHAR(32) | `PENDING`、`SUBMITTED`、`CONFIRMED`、`FAILED`、`UNKNOWN` |
| `request_hash` | VARCHAR(128) | 本次待上链摘要的哈希，用于幂等和核验 |
| `retry_count` | INT | 重试次数 |
| `last_error` | VARCHAR(1000) | 最后一次失败摘要，禁止写入密钥或敏感原文 |
| `created_time` | DATETIME | 创建时间 |
| `updated_time` | DATETIME | 最后状态更新时间 |

约束与索引建议：`transaction_hash` 唯一（允许空值）、`(business_type, business_id)` 唯一、`(status, updated_time)` 索引、`food_id` 索引。

## 4. 数据流与链数据库同步

```text
链上食品/追溯数据（traceNumber）
        ↓ 查询或确认
食品身份投影（food_id ↔ trace_number）
        ↓
特征快照 → FoodEvaluation（PENDING）→ EvaluationHistory(CREATED)
        ↓
BlockchainTransaction（PENDING，带 request_hash）
        ↓
FISCO BCOS / WeBASE 提交评价摘要或哈希
        ↓
交易回执：transaction_hash、block_number、status
        ↓
更新 BlockchainTransaction、FoodEvaluation 和 EvaluationHistory
```

未来 AI 评分流程应先从链上追溯记录及受控线下检测数据提取特征，在 `feature_snapshot` 固化输入；使用 `model_info` 中的明确版本生成评分；最后对包含 `traceNumber`、评价 ID、分数/等级、模型版本、特征摘要的规范化载荷计算哈希并存证。链上存摘要/哈希而非全量实验特征，避免成本、隐私和合约升级压力。

## 5. 一致性与补偿方案

不使用分布式事务跨越 MariaDB 和 FISCO BCOS。采用本地状态机加事务外盒（outbox）思想，并把 `blockchain_transaction` 作为可恢复任务记录。

### 数据库成功、链上失败

1. 在一个 MariaDB 本地事务中创建 `food_evaluation(PENDING)`、特征快照、历史 `CREATED` 和 `blockchain_transaction(PENDING)`。
2. 提交本地事务后异步或同步提交链上交易；失败时把交易记录标记为 `FAILED`，写入错误摘要和 `RETRY_SCHEDULED` 历史。
3. 重试任务以 `request_hash` 和唯一业务关系保证幂等；超过重试阈值后转人工核验，不把评价伪装为已可信存证。

### 链上成功、数据库保存失败

1. 提交前已持久化 `PENDING` 交易记录，因此进程重启后仍能恢复。
2. 若已拿到交易哈希但更新本地状态失败，使用交易哈希或 `request_hash` 查询 WeBASE/FISCO 回执并补写 `SUBMITTED`/`CONFIRMED`。
3. 如果在提交后进程直接故障，调度任务扫描陈旧 `PENDING` 记录，按 `request_hash` 查询或重放；不得盲目再次提交不带幂等标识的交易。

评价 API 的可信状态应由 `chain_status` 明确暴露：仅 `CONFIRMED` 可称为“已链上存证”；`PENDING` 与 `FAILED` 只表示本地评价结果。

## 6. AI 实验数据来源与可复现性

| 实验数据 | 推荐来源 | 保存位置 |
| --- | --- | --- |
| 食品标识、生产/运输/零售追溯事件 | FISCO BCOS 合约查询 | 链上为原始可信来源；本地可建查询投影 |
| 运输温湿度、检测结果、包装状态、供应商指标 | 经校验的业务采集或检测系统 | `feature_snapshot`，并记录 `source` |
| 模型版本与评估指标 | 模型训练流程 | `model_info` |
| 单次预测输入、输出与等级 | Evaluation 应用服务 | `feature_snapshot`、`food_evaluation` |
| 评分可信证明 | 规范化摘要哈希及交易回执 | FISCO BCOS 与 `blockchain_transaction` |

每次实验必须固定训练数据集版本、特征定义、模型版本和随机种子，并将它们写入模型元数据或受控实验记录。由链上 `traceNumber` 和交易哈希可回溯输入来源，由特征快照和模型版本可复现评分过程。

## 7. 对现有候选 SQL 的实施调整

`database/evaluation_tables.sql` 仍是未执行的初稿。正式执行前应在获授权的数据库迁移中：先创建食品身份投影；把 `food_evaluation.food_id` 改为 `BIGINT`；为 `feature_snapshot` 增加 `evaluation_id`；为 `food_evaluation` 增加 `model_id` 与 `chain_status`；扩展 `model_info` 指标字段；新增 `blockchain_transaction`。在没有这些映射和状态字段前，不应执行该脚本。
