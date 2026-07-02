package com.example.ShardedSagaWallet.entities;

public enum SagaStepStatus {
  PENDING,
  RUNNING,
  COMPLETED,
  FAILED,
  COMPENSATING,
  COMPENSATED,
  SKIPPED
}
