package com.example.ShardedSagaWallet.services.saga;

public interface SagaStepInterface {
 
  boolean executeStep(SagaContext context); 

  boolean compensateSteps(SagaContext context);

  String getStepName();
} 