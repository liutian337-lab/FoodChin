-- FoodChin Evaluation persistence schema for MariaDB 10.3.
-- This script is intentionally not executed by the application.
-- Apply only through an approved database migration after the food identity projection is accepted.

CREATE TABLE IF NOT EXISTS food_identity (
    id BIGINT NOT NULL AUTO_INCREMENT,
    trace_number VARCHAR(78) NOT NULL,
    created_time DATETIME NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_food_identity_trace_number (trace_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS food_evaluation (
    id BIGINT NOT NULL AUTO_INCREMENT,
    food_id BIGINT NOT NULL,
    score DECIMAL(5,2) NOT NULL,
    level VARCHAR(16) NOT NULL,
    confidence DECIMAL(5,4) NULL,
    model_id BIGINT NULL,
    model_version VARCHAR(64) NOT NULL,
    evaluation_time DATETIME NOT NULL,
    chain_status VARCHAR(32) NOT NULL,
    evaluation_hash VARCHAR(64) NOT NULL,
    created_time DATETIME NOT NULL,
    PRIMARY KEY (id),
    INDEX idx_food_evaluation_food_time (food_id, evaluation_time),
    INDEX idx_food_evaluation_chain_status (chain_status),
    INDEX idx_food_evaluation_model_id (model_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS evaluation_history (
    id BIGINT NOT NULL AUTO_INCREMENT,
    evaluation_id BIGINT NOT NULL,
    operation VARCHAR(64) NOT NULL,
    description VARCHAR(500) NULL,
    created_time DATETIME NOT NULL,
    PRIMARY KEY (id),
    INDEX idx_evaluation_history_evaluation_time (evaluation_id, created_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS feature_snapshot (
    id BIGINT NOT NULL AUTO_INCREMENT,
    evaluation_id BIGINT NOT NULL,
    food_id BIGINT NOT NULL,
    feature_name VARCHAR(128) NOT NULL,
    feature_value VARCHAR(512) NOT NULL,
    source VARCHAR(128) NULL,
    created_time DATETIME NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_feature_snapshot_evaluation_name (evaluation_id, feature_name),
    INDEX idx_feature_snapshot_food_time (food_id, created_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS model_info (
    id BIGINT NOT NULL AUTO_INCREMENT,
    model_name VARCHAR(128) NOT NULL,
    version VARCHAR(64) NOT NULL,
    algorithm VARCHAR(64) NOT NULL,
    accuracy DECIMAL(6,5) NULL,
    metrics_json LONGTEXT NULL,
    created_time DATETIME NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_model_info_name_version (model_name, version)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS blockchain_transaction (
    id BIGINT NOT NULL AUTO_INCREMENT,
    business_type VARCHAR(64) NOT NULL,
    business_id BIGINT NOT NULL,
    food_id BIGINT NULL,
    transaction_hash VARCHAR(128) NULL,
    contract_address VARCHAR(128) NOT NULL,
    block_number BIGINT NULL,
    status VARCHAR(32) NOT NULL,
    request_hash VARCHAR(128) NOT NULL,
    evaluation_hash VARCHAR(64) NOT NULL,
    retry_count INT NOT NULL DEFAULT 0,
    last_error VARCHAR(1000) NULL,
    created_time DATETIME NOT NULL,
    updated_time DATETIME NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_blockchain_business (business_type, business_id),
    UNIQUE KEY uk_blockchain_transaction_hash (transaction_hash),
    INDEX idx_blockchain_transaction_status_time (status, updated_time),
    INDEX idx_blockchain_transaction_food_id (food_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
