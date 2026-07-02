package com.example.ShardedSagaWallet.services.saga;

public interface SagaStep {
 
  boolean executeStep(SagaContext context); 

  boolean compensateSteps(SagaContext context);

  String getStepName();
} 