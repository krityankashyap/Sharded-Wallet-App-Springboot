package com.example.ShardedSagaWallet.entities;

public enum SagaStatus {

  STARTED,
  RUNNING,
  COMPLETED,
  FAILED,
  COMPENSATING,
  COMPENSATED
}
