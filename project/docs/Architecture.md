# FoodChin Architecture

## 1. Project background

FoodChin is a food traceability system built on Spring Boot, Vue, Solidity, FISCO BCOS, and WeBASE. The original system supports role-based access, food creation, traceability queries, and recording food circulation data on a blockchain.

The project is evolving incrementally into a research-ready platform. Existing food traceability behavior, API routes, smart contracts, and frontend interactions remain compatible while backend responsibilities are made explicit.

## 2. Original architecture issues

The original implementation concentrated HTTP handling, validation, food processing, and WeBASE contract requests in one controller. This made the code difficult to test and extend.

Main limitations were:

- Controllers were too large and contained business workflow logic.
- Food and traceability services were coupled to the concrete WeBASE implementation.
- Blockchain endpoint, contract, account, and network settings were embedded in code or flat configuration.
- Response and error handling did not have a clear, extensible boundary.
- The structure did not provide clear extension points for evaluation, model management, or experiment services.

## 3. Refactored architecture

```text
Vue frontend
    |
    v
Controller
    |
    v
Application
    |
    v
Domain Service
    |
    v
Infrastructure
    |
    +-- Database (future integration)
    +-- Blockchain
    |     |
    |     +-- BlockchainService
    |     +-- FiscoBlockchainServiceImpl
    |     +-- WeBASE / FISCO BCOS
    |
    +-- External Service (future integration)
```

The architecture preserves legacy API paths and payload structures. New boundaries are introduced around existing functions instead of replacing the existing traceability workflow.

## 4. Module responsibilities

### Controller

Controllers are the HTTP adaptation layer. They receive requests, bind request DTOs, delegate to Application Services, and return compatible responses. Controllers do not perform blockchain calls or business workflow decisions.

### Application

Application Services coordinate request DTO conversion, response VO mapping, and calls to domain services. They provide the appropriate location for future cross-service business processes without changing controllers.

### Domain

Domain Services contain the current food and traceability workflow rules, including validation, trace state interpretation, and construction of legacy-compatible food and trace responses.

### Infrastructure

Infrastructure adapters implement external capabilities. The blockchain adapter owns WeBASE request construction, ABI use, contract invocation, account selection, timeout configuration, and blockchain error conversion. Database and other external integrations will follow the same boundary when added.

## 5. Blockchain invocation flow

```text
Food or traceability request
    |
    v
FoodService / TraceService
    |
    v
BlockchainService (interface)
    |
    v
FiscoBlockchainServiceImpl
    |
    v
WeBASE-Front
    |
    v
FISCO BCOS / Trace contract
```

Business services depend on the `BlockchainService` abstraction rather than WeBASE HTTP details. `FiscoBlockchainServiceImpl` is the only component that constructs the WeBASE request body and reads blockchain transport configuration.

## 6. Future extension design

The following modules can be added without changing the current traceability APIs:

- **Evaluation**: coordinates food quality evaluation requests.
- **Evaluation History**: records and queries evaluation results over time.
- **Model Management**: stores model metadata, versions, and experiment references.
- **AI Prediction**: integrates a machine-learning prediction service through an external-service adapter.
- **Blockchain Evaluation**: stores evaluation evidence and status through the blockchain infrastructure port.

Future modules should use the same Controller -> Application -> Domain -> Infrastructure flow. New APIs must be versioned or additive when a legacy response shape cannot be preserved.

## 7. Design principles

- Preserve existing functions, contracts, routes, and frontend compatibility.
- Evolve incrementally instead of replacing the running system.
- Isolate modules and external technology details behind explicit interfaces.
- Keep configuration externalized and environment-overridable.
- Keep experiments reproducible through documented configuration, model versions, and measured execution flows.
- Prefer additive changes and verified builds for each architecture sprint.
