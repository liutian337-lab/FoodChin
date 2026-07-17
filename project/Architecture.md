# FoodChain Backend Architecture

## Package structure

```text
demo
├── application
│   ├── FoodApplicationService.java
│   └── TraceApplicationService.java
├── common
│   └── Result.java
├── config
│   ├── BlockchainProperties.java
│   └── MvcConfig.java
├── controller
│   ├── FoodController.java
│   ├── TraceController.java
│   ├── BlockchainController.java
│   └── EvaluationController.java
├── dto
│   ├── request
│   ├── response
│   └── vo
├── exception
│   └── GlobalExceptionHandler.java
└── service
    ├── FoodService.java
    ├── TraceService.java
    ├── BlockchainService.java
    └── EvaluationService.java
```

## Dependency graph

```text
FoodController ──> FoodApplicationService ──> FoodService ──> BlockchainService ──> WeBASE-Front
TraceController ─> TraceApplicationService ──> TraceService ─┬> FoodService
                                 └> BlockchainService ──> WeBASE-Front
BlockchainController
EvaluationController ───────────> EvaluationService
GlobalExceptionHandler ─────────> Result<T>
BlockchainProperties <────────── application.yml
```

## Controller responsibilities

`FoodController` retains the food-facing legacy routes: `/index`, `/userinfo`, `/produce`, `/foodlist`, `/food`, and `/producing`.

`TraceController` retains the traceability legacy routes: `/trace`, `/adddistribution`, `/addretail`, `/newtracelist`, `/distributing`, and `/retailing`.

`BlockchainController` is reserved for future blockchain-facing HTTP endpoints. It exposes no route in this sprint.

`EvaluationController` is reserved for the later quality-evaluation module. It exposes no route in this sprint.

## Service responsibilities

`FoodService` performs legacy food request validation and transforms chain responses into the existing frontend response payloads.

`TraceService` performs legacy trace update and query orchestration, including distribution and retail transition payloads.

`FoodApplicationService` and `TraceApplicationService` convert controller DTOs into the existing service payloads and coordinate service calls. They contain no new business rules.

`BlockchainService` is the exclusive WeBASE-Front client. It builds contract invocation requests, applies the configured timeout, and provides read helpers for the existing Trace contract.

`EvaluationService` is intentionally empty in Sprint 1.1. No AI or evaluation feature is introduced.

## Compatibility

All pre-existing route paths, HTTP methods, request field names, and successful-response payload shapes are retained. `Result<T>` is used by the global exception boundary; legacy endpoints continue returning their established payloads so that the existing Vue frontend remains compatible.

## Configuration

All blockchain environment values are located in `src/main/resources/application.yml` under `blockchain`. Every value can be overridden by its corresponding `BLOCKCHAIN_*` environment variable. `BlockchainProperties` binds those settings through `@ConfigurationProperties`.
