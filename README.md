<div align="center">

<br/>
# Midas Transaction Service

**A high-performance, event-driven financial transaction processing engine**

[![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/projects/jdk/17/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.5-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Apache Kafka](https://img.shields.io/badge/Apache_Kafka-3.1-231F20?style=for-the-badge&logo=apache-kafka&logoColor=white)](https://kafka.apache.org/)
[![Maven](https://img.shields.io/badge/Maven-Build-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)](https://maven.apache.org/)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue?style=for-the-badge)](LICENSE)

<br/>

*Consume → Validate → Reconcile → Incentivize → Persist*

<br/>

</div>

---

## 📖 Overview

**Midas Transaction Service** is an asynchronous, Kafka-driven microservice that handles end-to-end financial transaction processing with built-in balance reconciliation, external incentive integration, and full audit logging.

When a transaction event lands on the Kafka `transactions` topic, Midas:

1. **Validates** the payload and enforces business constraints
2. **Checks balances** — rejects if the sender has insufficient funds
3. **Fetches incentives** from an external rewards API
4. **Reconciles balances** — debits sender, credits recipient (+ incentive)
5. **Persists** a tamper-evident `TransactionRecord` for full audit traceability

---

## ✨ Features

| Feature | Description |
|---------|-------------|
| ⚡ **Async Kafka Consumer** | Subscribes to `transactions` topic; processes events in real time |
| 🔒 **Business Validation** | Null checks, balance sufficiency checks, and entity existence guards |
| 💰 **Incentive Integration** | Calls external `transaction-incentive-api` to fetch reward amounts |
| ⚖️ **Balance Reconciliation** | Atomic debit/credit with Spring `@Transactional` guarantees |
| 📋 **Audit Logging** | Every processed transaction written to `TransactionRecord` with timestamp & status |
| 🌐 **REST API** | Query live balances and transaction records via HTTP endpoints |
| 🧪 **Integration Tests** | Kafka + Testcontainers test suite covering 5 real-world task scenarios |

---

## 🏗️ Architecture

```
                        ┌─────────────────────────────────────────────┐
                        │            Midas Transaction Service         │
                        │                                             │
   Kafka Topic          │  ┌─────────────────┐                        │
  "transactions"  ────► │  │TransactionListener│                       │
                        │  └────────┬────────┘                        │
                        │           │ TransactionDto (JSON)            │
                        │  ┌────────▼────────┐   ┌─────────────────┐  │
                        │  │TransactionService│──►│TransactionValidator│ │
                        │  └────────┬────────┘   └─────────────────┘  │
                        │           │                                  │
                        │    ┌──────┴──────┐                           │
                        │    │             │                           │
                        │ ┌──▼──┐    ┌────▼──────────┐                │
                        │ │User │    │IncentiveClient │──► External API│
                        │ │Repo │    └───────────────┘                │
                        │ └──┬──┘                                     │
                        │    └──► H2 / JPA ──► TransactionRecord      │
                        │                                             │
                        │  REST API: GET /balance, GET /api/incentives│
                        └─────────────────────────────────────────────┘
```

---

## 🗂️ Project Structure

```
midas-transaction-service/
├── src/
│   ├── main/
│   │   ├── java/com/midas/core/
│   │   │   ├── MidasCoreApplication.java       # Spring Boot entry point
│   │   │   ├── config/
│   │   │   │   ├── KafkaConfig.java            # Kafka consumer/producer beans
│   │   │   │   └── RestTemplateConfig.java     # HTTP client bean
│   │   │   ├── controller/
│   │   │   │   ├── BalanceController.java       # GET /balance
│   │   │   │   └── IncentiveController.java    # GET /api/incentives/{id}
│   │   │   ├── dto/
│   │   │   │   ├── TransactionDto.java         # Kafka message payload
│   │   │   │   └── Balance.java                # Balance response wrapper
│   │   │   ├── listener/
│   │   │   │   └── TransactionListener.java    # @KafkaListener
│   │   │   ├── model/
│   │   │   │   ├── Transaction.java            # Incentive request model
│   │   │   │   ├── TransactionRecord.java      # JPA audit entity
│   │   │   │   ├── Incentive.java              # External API response model
│   │   │   │   └── User.java                   # Account holder entity
│   │   │   ├── repository/
│   │   │   │   ├── UserRepository.java
│   │   │   │   └── TransactionRecordRepository.java
│   │   │   └── service/
│   │   │       ├── TransactionService.java     # Core business logic
│   │   │       ├── TransactionValidator.java   # Pre-processing guards
│   │   │       └── IncentiveClient.java        # External API client
│   │   └── resources/
│   │       └── application.yml
│   └── test/
│       ├── java/com/midas/core/
│       │   ├── TaskOneTests.java – TaskFiveTests.java   # Integration tests
│       │   └── support/UserTestData.java
│       └── resources/test_data/
│           ├── task_two_transactions.jsonl
│           ├── task_three_transactions.jsonl
│           └── task_four_transactions.jsonl
├── services/
│   └── transaction-incentive-api.jar           # External incentive service
├── Postman_Collection.json
└── pom.xml
```

---

## ⚙️ Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 17 |
| Framework | Spring Boot 3.2.5 |
| Messaging | Apache Kafka + Spring Kafka 3.1.4 |
| Persistence | Spring Data JPA + H2 (in-memory) |
| HTTP Client | Spring RestTemplate |
| JSON | Google Gson |
| Testing | JUnit 5 · Spring Kafka Test · Testcontainers 1.19.1 |
| Build | Apache Maven |

---

## 🚀 Getting Started

### Prerequisites

- **Java 17+**
- **Maven 3.8+**
- **Apache Kafka** running on `localhost:9092`
- **External Incentive API** JAR (included in `/services`)

### 1 — Start the External Incentive API

```bash
java -jar services/transaction-incentive-api.jar
# Starts on http://localhost:8080
```

### 2 — Start Kafka (Docker)

```bash
docker run -d --name kafka \
  -p 9092:9092 \
  -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 \
  -e KAFKA_LISTENERS=PLAINTEXT://0.0.0.0:9092 \
  -e KAFKA_ZOOKEEPER_CONNECT="" \
  -e KAFKA_PROCESS_ROLES=broker \
  -e KAFKA_NODE_ID=1 \
  apache/kafka:3.7.0
```

> Or use your existing Kafka instance — just ensure `localhost:9092` is reachable.

### 3 — Run the Application

```bash
mvn spring-boot:run
```

The service starts on **port `33400`**.

### 4 — Run Tests

```bash
mvn test
```

All tests use **Testcontainers** — a real Kafka broker is spun up automatically. No external setup needed for tests.

---

## 🌐 REST API

### `GET /balance`

Returns the current balance of a user account.

```bash
curl "http://localhost:33400/balance?userId=1"
```

**Response:**
```json
{
  "balance": 4250.75
}
```

---

### `GET /api/incentives/{id}`

Returns the full `TransactionRecord` (including incentive) for a given record ID.

```bash
curl "http://localhost:33400/api/incentives/42"
```

**Response:**
```json
{
  "id": 42,
  "txnId": "TXN-20001",
  "amount": 500.00,
  "incentive": 25.50,
  "status": "VALID",
  "timestamp": 1745808000000,
  "sender": { "id": 1, "balance": 3750.00 },
  "recipient": { "id": 2, "balance": 6525.50 }
}
```

---

## 📨 Publishing a Transaction to Kafka

Transactions are consumed from the `transactions` Kafka topic. The expected JSON payload:

```json
{
  "txnId": "TXN-00101",
  "senderId": 1,
  "recipientId": 2,
  "amount": 150.00
}
```

**Publish via Kafka CLI:**

```bash
kafka-console-producer.sh \
  --bootstrap-server localhost:9092 \
  --topic transactions
# Then paste the JSON and press Enter
```

---

## 🔄 Transaction Processing Flow

```
Kafka Message (JSON)
        │
        ▼
TransactionListener.listen()
        │  gson.fromJson → TransactionDto
        ▼
TransactionService.processIncomingTransaction(dto)
        │
        ├─► TransactionValidator.validate(dto)
        │       └─ FAIL → discard, return false
        │
        ├─► UserRepository.findById(senderId, recipientId)
        │       └─ missing → discard, return false
        │
        ├─► sender.balance >= dto.amount ?
        │       └─ NO → discard, return false
        │
        ├─► IncentiveClient.fetchIncentive(transaction)
        │       └─ calls external incentive API → returns incentiveAmount
        │
        ├─► sender.balance   -= amount
        │   recipient.balance += amount + incentiveAmount
        │   userRepository.save(sender, recipient)
        │
        └─► TransactionRecord{status=VALID} → persist
```

---

## 🧪 Test Scenarios

| Test Class | Scenario |
|------------|----------|
| `TaskOneTests` | Basic transaction consumption → balance update |
| `TaskTwoTests` | Multi-transaction batch reconciliation |
| `TaskThreeTests` | Edge cases: zero amount, unknown users, duplicate txnIds |
| `TaskFourTests` | Incentive integration with external API |
| `TaskFiveTests` | End-to-end flow with full audit record verification |

All tests use **Spring Kafka Test** + **Testcontainers** for a production-faithful Kafka broker.

---

## 📋 Postman Collection

Import `Postman_Collection.json` into Postman to get pre-built requests for all REST endpoints with example payloads.

---

## 🔧 Configuration Reference

```yaml
# application.yml

spring:
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: midas-core-group
      auto-offset-reset: earliest

  datasource:
    url: jdbc:h2:mem:midasdb;DB_CLOSE_DELAY=-1
    username: sa

server:
  port: 33400

midas:
  kafka:
    transactions-topic: transactions
  incentive:
    base-url: http://localhost:8080
```

| Property | Default | Description |
|----------|---------|-------------|
| `spring.kafka.bootstrap-servers` | `localhost:9092` | Kafka broker address |
| `midas.kafka.transactions-topic` | `transactions` | Topic to consume from |
| `midas.incentive.base-url` | `http://localhost:8080` | External incentive API base URL |
| `server.port` | `33400` | HTTP server port |

---

## 👤 Author

**Priyam Jaiswal**

[![GitHub](https://img.shields.io/badge/GitHub-PriyamJaiswal-181717?style=flat-square&logo=github)](https://github.com/PriyamJaiswal)
[![Email](https://img.shields.io/badge/Email-priyamj608%40gmail.com-D14836?style=flat-square&logo=gmail&logoColor=white)](mailto:priyamj608@gmail.com)

---

<div align="center">

*Built with ☕ Java and a passion for clean, event-driven systems*

</div>
