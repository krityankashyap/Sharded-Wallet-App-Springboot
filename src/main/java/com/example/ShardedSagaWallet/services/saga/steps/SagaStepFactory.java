package com.example.ShardedSagaWallet.services.saga.steps;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.example.ShardedSagaWallet.entities.SagaStep;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SagaStepFactory {

  private final Map<String, SagaStep> sagaStepMap;



  public static enum SagaStepType {
    DEBIT_SOURCE_WALLET_STEP,
    CREDIT_DESTINATION_WALLET_STEP,
    UPDATE_TRANSACTION_WALLET_STEP,
  }
  
  public SagaStep getSagaStep(String stepName) {
    return sagaStepMap.get(stepName);
  }
}
