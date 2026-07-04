package com.example.ShardedSagaWallet.services.saga;

import com.example.ShardedSagaWallet.entities.SagaInstance;

public interface SagaOrchestrator {
 
  Long startSaga(SagaContext context); // returns the saga instance id

  boolean executeStep(Long sagaInstanceId, String stepName); // returns true if the step executed successfully, false otherwise

  boolean compensateStep(Long sagaInstanceId, String stepName); // returns true if the compensation executed successfully, false otherwise
  
  SagaInstance getSagaInstance(Long sagaInstanceId);

  void compensateSaga(Long sagaInstanceId);

  void failSaga(Long sagaInstanceId);

  void completeSaga(Long sagaInstanceId);
} 