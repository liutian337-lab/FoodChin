# FoodChin Evaluation 数据库初始化指南

## 1. 环境要求

- Ubuntu 虚拟机中的 MariaDB 10.3 或兼容版本。
- 已存在数据库 `foodchin`，并使用具备建表和索引权限的受控数据库账号。
- Spring Boot 运行主机能够访问 MariaDB；若使用 SSH 隧道，`FOODCHIN_DB_URL` 应指向隧道本地端口。
- 不要使用数据库管理员账户作为应用运行账户；应用账户仅应具备运行时所需的最小权限。

FoodChin 不会自动建表。`application.yml` 固定使用 `spring.jpa.hibernate.ddl-auto: validate`：应用启动时只校验实体与表结构，不会创建、更新或删除表。

## 2. 初始化步骤

1. 在执行前备份目标数据库，并确认目标为 `foodchin`，而非生产环境的其他库。
2. 将项目中的 `database/evaluation_tables.sql` 复制到可访问 MariaDB 的 Ubuntu 环境。
3. 使用经过授权的数据库运维账号执行脚本。示例命令中的账号和主机请按实际环境替换：

```bash
mysql --host=<db-host> --port=<db-port> --user=<migration-user> --password foodchin < evaluation_tables.sql
```

4. 脚本使用 `CREATE TABLE IF NOT EXISTS`，但这不等于自动修复已有的错误结构。若表已经存在，应先通过下方验证 SQL 比对字段，再由运维人员制定显式迁移。
5. 不要在应用启动时执行该脚本；也不要将 `ddl-auto` 改成 `create` 或 `update`。

脚本会创建六张表：`food_identity`、`food_evaluation`、`evaluation_history`、`feature_snapshot`、`model_info` 和 `blockchain_transaction`。其中 `food_identity` 是本地 `food_id` 与链上 `trace_number` 的映射前置表。

## 3. 初始化后验证 SQL

以下语句只读取元数据和数据，不修改任何记录：

```sql
USE foodchin;

SHOW TABLES LIKE 'food_%';
SHOW TABLES LIKE 'evaluation_history';
SHOW TABLES LIKE 'feature_snapshot';
SHOW TABLES LIKE 'model_info';
SHOW TABLES LIKE 'blockchain_transaction';

DESCRIBE food_identity;
DESCRIBE food_evaluation;
DESCRIBE evaluation_history;
DESCRIBE feature_snapshot;
DESCRIBE model_info;
DESCRIBE blockchain_transaction;

SHOW INDEX FROM food_evaluation;
SHOW INDEX FROM feature_snapshot;
SHOW INDEX FROM model_info;
SHOW INDEX FROM blockchain_transaction;
```

重点核验：所有主键均为 `BIGINT AUTO_INCREMENT`；`food_evaluation.food_id`、`feature_snapshot.evaluation_id` 均为 `BIGINT`；`model_info.metrics_json` 为 `LONGTEXT`；以及 `blockchain_transaction` 包含交易哈希、合约地址、区块高度、状态和时间字段。

## 4. Spring Boot 启动验证

1. 为应用进程设置连接信息，不把密码提交到 Git：

```bash
export FOODCHIN_DB_URL='jdbc:mariadb://<db-host>:<db-port>/foodchin?useUnicode=true&characterEncoding=utf8'
export FOODCHIN_DB_USERNAME='<application-user>'
export FOODCHIN_DB_PASSWORD='<application-password>'
```

2. 启动 Spring Boot 应用并检查日志。成功时 Hibernate 完成 schema validation，且日志中不出现 `Schema-validation`、`Table not found` 或数据库认证失败。
3. 在不接入 AI 与区块链评价存证前，仅使用已有 `GET /evaluations` 接口确认应用可读取空列表或已有评价；不要为验证目的插入测试业务数据到共享环境。
4. 若启动失败，先用第 3 节的 `DESCRIBE` 输出与 JPA 实体比对；不要通过改为 `ddl-auto=update` 绕过问题。

## 5. 运维边界

- SQL 的执行、备份、权限授予和生产环境变更由数据库管理员授权并完成。
- 当前 Food/Trace 业务仍以链上 `traceNumber` 为业务标识；Evaluation 入库前需先建立 `food_identity` 映射。
- `blockchain_transaction.status` 只有为 `CONFIRMED` 时，评价结果才能被标记为已完成链上可信存证。
