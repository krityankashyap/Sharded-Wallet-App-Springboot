# ShardedSagaWallet

A Spring Boot wallet service that survives two hard problems most tutorials skip: **horizontally sharded data** and **distributed transactions across shards**.

Money moves between wallets that may live on different databases. There is no XA, no two-phase commit — just a Saga orchestrator that walks each step forward, and walks it back if something breaks.

## The idea

Traditional wallet apps assume one database. That breaks the day your user table crosses a hundred million rows. So:

- **Users and wallets are sharded** across two MySQL databases via Apache ShardingSphere.
- Wallets shard on `user_id` so a user's wallet always lands on the same node as the user — no cross-shard joins for common reads.
- **Transfers between wallets may cross shards.** You cannot wrap that in a `@Transactional`. So we use the Saga pattern: each step is a local transaction, and every step has a compensating action.

If crediting the destination fails after debiting the source, the orchestrator plays the compensation chain in reverse. The money doesn't vanish.

## Architecture

```
Client ──► TransactionController ──► TransferSagaService
                                            │
                                            ▼
                              SagaOrchestrator (persists state)
                                            │
                          ┌─────────────────┼─────────────────┐
                          ▼                 ▼                 ▼
               DebitSourceWallet   CreditDestinationWallet   UpdateTransaction
                          │                 │                 │
                          ▼                 ▼                 ▼
                     shardwallet1      shardwallet2      shardwallet1/2
```

Every step, every saga instance, and every state transition is persisted (`saga_instance`, `saga_step`). If the JVM dies mid-transfer, the state is recoverable.

## Tech

- Java 17, Spring Boot 4.1
- Apache ShardingSphere 5.5.2 (JDBC-level sharding, Snowflake IDs)
- MySQL 8, HikariCP
- Gradle, Lombok

## Sharding rules

Defined in `src/main/resources/Sharding.yml`:

| Table          | Shard key   | Algorithm            |
|----------------|-------------|----------------------|
| `users`        | `id`        | `id % 2 + 1`         |
| `wallet`       | `user_id`   | `user_id % 2 + 1`    |
| `transaction`  | `id`        | `id % 2 + 1`         |
| `saga_instance`| `id`        | `id % 2 + 1`         |
| `saga_step`    | `id`        | `id % 2 + 1`         |

The wallet's shard follows its owner — so single-user reads never fan out.

## Run it

Create two MySQL databases:

```sql
CREATE DATABASE shardwallet1;
CREATE DATABASE shardwallet2;
```

Update credentials in `src/main/resources/Sharding.yml`, then:

```bash
./gradlew bootRun
```

## API

```
POST /users/create              # create a user
POST /wallets                   # create a wallet (userId in body)
GET  /wallets/{id}/balance      # check balance
POST /wallets/{userId}/credit   # local credit
POST /wallets/{userId}/debit    # local debit
POST /transactions              # transfer between wallets (returns sagaInstanceId)
```

## Project layout

```
services/
├── TransferSagaService.java       # kicks off the saga
├── saga/
│   ├── SagaOrchestratorImpl.java  # executes + compensates steps
│   ├── SagaContext.java           # payload carried between steps
│   └── steps/                     # DebitSource, CreditDestination, UpdateTransaction
├── WalletService.java
├── TransactionService.java
└── UserService.java
```

## Status

A working reference implementation. Rough edges: no retries with backoff on step failure, no idempotency keys on the transfer endpoint, no auth. Contributions welcome — the interesting problem is here, the polish isn't.
