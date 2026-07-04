package com.example.ShardedSagaWallet.entities;

public enum SagaStatus {

  STARTED,
  PENDING,
  RUNNING,
  COMPLETED,
  FAILED,
  COMPENSATING,
  COMPENSATED
}
