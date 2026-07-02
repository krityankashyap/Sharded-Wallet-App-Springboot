package com.example.ShardedSagaWallet.services.saga.steps;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ShardedSagaWallet.services.saga.SagaContext;
import com.example.ShardedSagaWallet.services.saga.SagaStep;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateTranactionalSteps implements SagaStep {
  
  @Override
  @Transactional
  public boolean executeStep(SagaContext context) {
    
    
    return true; // Return true if the step executed successfully, false otherwise
  }

  @Override
  @Transactional
  public boolean compensateSteps(SagaContext context) {
    return true; // Return true if the compensation executed successfully, false otherwise
  }

  @Override
  public String getStepName() {
    return "UpdateTranactionalSteps";
  }
}
