# FoodChin 端到端食品评价接口测试指南

## 1. 前置条件

- Spring Boot 已启动在 `http://localhost:8080`，并能够连接 MariaDB。
- FastAPI 服务已启动，`AI_SERVICE_URL` 指向该服务。
- FastAPI 已通过 `AI_MODEL_PATH` 加载经过训练的 Random Forest 模型文件。
- 请求中的 `features` 键必须与模型产物中保存的特征名完全匹配；例如模型训练使用 `temperature`、`humidity`、`storage_time`，测试也必须提供这三个键。
- `foodId` 应对应已建立的食品身份映射。当前接口不自动创建食品身份。
- FISCO/WeBASE 所配置的评价存证合约 ABI 必须包含 `BLOCKCHAIN_EVALUATION_ANCHOR_FUNCTION` 指定的方法（默认 `recordEvaluation`），并返回交易哈希。

## 2. 预测并创建可信评价

### 请求

```text
POST http://localhost:8080/evaluations/predict
Content-Type: application/json
```

Postman 不需要认证 Header。若部署了网关或鉴权组件，应按部署环境补充认证 Header。

```json
{
  "foodId": 7,
  "features": {
    "temperature": 4.2,
    "humidity": 68.0,
    "storage_time": 12.0
  }
}
```

`EvaluationPredictDTO` 字段：

| 字段 | Java 类型 | 必填 | 示例 |
| --- | --- | --- | --- |
| `foodId` | `Long` | 是，正整数 | `7` |
| `features` | `Map<String, BigDecimal>` | 是 | `{ "temperature": 4.2 }` |

### 预期成功响应

外层是统一 `Result<T>`。`data.status` 为 `SUCCESS` 时才表示已完成链上存证。

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 101,
    "evaluationId": 101,
    "foodId": 7,
    "score": 91.2,
    "level": "A",
    "confidence": 0.91,
    "modelVersion": "rf-v1",
    "chainStatus": "SUCCESS",
    "status": "SUCCESS",
    "evaluationHash": "<64-character-sha256>",
    "transactionHash": "0x...",
    "evaluationTime": "2026-07-18T17:00:00",
    "createdTime": "2026-07-18T17:00:00"
  },
  "timestamp": 0
}
```

`FAILED` 表示 AI 预测已写入评价记录，但区块链写入或交易回执失败。此时应查询历史和交易映射，不应把结果当作已可信存证。

## 3. 查询结果和过程历史

```text
GET http://localhost:8080/evaluation/result/7
GET http://localhost:8080/evaluations/101/history
```

历史中应至少出现：

- `CREATED`
- `BLOCKCHAIN_SUCCESS` 或 `BLOCKCHAIN_FAILED`
- `PROCESS_METRICS`，其中包含 `aiLatencyMs`、`blockchainLatencyMs` 和 `totalResponseTimeMs`

## 4. MariaDB 验证 SQL

将 `101` 和 `7` 替换为实际响应中的 `evaluationId` 与 `foodId`。下列 SQL 均为只读查询。

```sql
USE foodchin;

SELECT id, food_id, score, level, confidence, model_version,
       evaluation_hash, chain_status, evaluation_time, created_time
FROM food_evaluation
WHERE id = 101;

SELECT business_id AS evaluation_id, evaluation_hash, transaction_hash,
       contract_address, block_number, status, created_time, updated_time
FROM blockchain_transaction
WHERE business_type = 'EVALUATION_ANCHOR'
  AND business_id = 101;

SELECT evaluation_id, food_id, feature_name, feature_value, source, created_time
FROM feature_snapshot
WHERE evaluation_id = 101
ORDER BY id;

SELECT evaluation_id, operation, description, created_time
FROM evaluation_history
WHERE evaluation_id = 101
ORDER BY created_time, id;

SELECT id, model_name, version, algorithm, accuracy, created_time
FROM model_info
WHERE version = 'rf-v1';
```

## 5. 端到端验收清单

1. FastAPI 返回有效的 `score`、`level`、`confidence` 和 `modelVersion`。
2. `food_evaluation` 出现对应记录，且 `evaluation_hash` 为 64 位 SHA-256 十六进制字符串。
3. `feature_snapshot` 保存本次请求的每个特征，`source` 为 `AI_INPUT`。
4. `model_info` 存在预测所用模型名称和版本。
5. `blockchain_transaction` 存在评价映射；成功时具有 `transaction_hash`、`block_number` 与 `SUCCESS` 状态。
6. `/evaluation/result/{foodId}` 返回最新评价，且 `/evaluations/{evaluationId}/history` 返回完整状态和耗时记录。
