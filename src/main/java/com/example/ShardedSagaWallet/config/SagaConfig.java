package com.example.ShardedSagaWallet.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.ShardedSagaWallet.services.saga.SagaStep;
import com.example.ShardedSagaWallet.services.saga.steps.CreditDestinationWalletStep;
import com.example.ShardedSagaWallet.services.saga.steps.DebitSourceWalletStep;
import com.example.ShardedSagaWallet.services.saga.steps.UpdateTranactionalSteps;
import com.example.ShardedSagaWallet.services.saga.steps.SagaStepFactory.SagaStepType;

@Configuration
public class SagaConfig {

  @Bean
  public Map<String, SagaStep> sagaStepMap(
    DebitSourceWalletStep debitSourceWalletStep,
    CreditDestinationWalletStep creditDestinationWalletStep,
    UpdateTranactionalSteps updateTranactionalSteps
  ) {
    Map<String, SagaStep> map= new HashMap<>();
    map.put(SagaStepType.CREDIT_DESTINATION_WALLET_STEP.toString() , creditDestinationWalletStep);
    map.put(SagaStepType.DEBIT_SOURCE_WALLET_STEP.toString() , debitSourceWalletStep);
    map.put(SagaStepType.UPDATE_TRANSACTION_WALLET_STEP.toString() , updateTranactionalSteps);

    return map;
  }

}
