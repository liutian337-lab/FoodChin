# EvaluationRegistry 部署说明

`EvaluationRegistry.sol` 是独立于 `Trace.sol` 的评价可信存证合约；部署它不会修改或替换现有食品追溯合约。

## 编译和部署

1. 使用与现有项目兼容的 Solidity 0.4.25 编译器编译 `EvaluationRegistry.sol`。
2. 通过 WeBASE 部署新合约到与 Trace 相同的 FISCO BCOS Group（当前默认 Group ID 为 `1`）。
3. 保存部署后的新合约地址、ABI 和部署交易哈希。
4. 在后续后端配置中新增独立的 Evaluation 合约地址和 ABI；不要覆盖 Trace 的地址或 ABI。

## 参数编码约定

| 合约参数 | 后端值 |
| --- | --- |
| `foodId` | 食品链上 `traceNumber`，不是 MariaDB 自增主键 |
| `evaluationHash` | 64 位 SHA-256 十六进制值转成 `bytes32` |
| `score` | 分数乘以 100，例如 `84.81 → 8481` |
| `confidence` | 置信度乘以 10,000，例如 `0.9649 → 9649` |
| `evaluationTime` | Unix 秒时间戳 |

## Java 调用签名

```text
recordEvaluation(uint256,bytes32,uint256,string,uint256,string,uint256)
getEvaluation(uint256)
getFoodEvaluations(uint256)
```

`recordEvaluation` 的 `evaluationId` 是链上递增 ID；MariaDB 的 Evaluation ID 应通过 `blockchain_transaction` 与该链上交易哈希关联，而不能假定二者相同。
