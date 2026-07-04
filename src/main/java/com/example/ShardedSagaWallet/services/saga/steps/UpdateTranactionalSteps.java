package com.example.ShardedSagaWallet.services.saga.steps;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ShardedSagaWallet.entities.Transaction;
import com.example.ShardedSagaWallet.entities.TransactionalStatus;
import com.example.ShardedSagaWallet.repository.TransactionalRepository;
import com.example.ShardedSagaWallet.services.saga.SagaContext;
import com.example.ShardedSagaWallet.services.saga.SagaStep;
import com.example.ShardedSagaWallet.services.saga.steps.SagaStepFactory.SagaStepType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateTranactionalSteps implements SagaStep {
  
  private final TransactionalRepository transactionalRepository;


  @Override
  @Transactional
  public boolean executeStep(SagaContext context) {
    Long transactionalId= context.getLong("transactionalId");

    log.info("Updating transactional steps for transactionalId: {}", transactionalId);

   Transaction transaction= transactionalRepository.findById(transactionalId).orElseThrow(() -> new RuntimeException("Transaction not found"));

   context.put("OriginalTransactionStatus", transaction.getStatus()); 

   transaction.setStatus(TransactionalStatus.SUCCESSED);
   transactionalRepository.save(transaction);

   log.info("Transactional steps updated successfully for transactionalId: {}. New status: {}", transactionalId, transaction.getStatus());
    return true; // Return true if the step executed successfully, false otherwise
  }

  @Override
  @Transactional
  public boolean compensateSteps(SagaContext context) {
    Long transactionalId= context.getLong("transactionalId");

    TransactionalStatus transactionalStatusBeforeCompensate = TransactionalStatus.valueOf(context.getString ("originalTransactionStatus")); 

    log.info("Compensating transactional steps for transactionalId: {}", transactionalId);

    Transaction transaction= transactionalRepository.findById(transactionalId).orElseThrow(() -> new RuntimeException("Transaction not found"));

    transaction.setStatus(transactionalStatusBeforeCompensate);
    transactionalRepository.save(transaction);
  
    log.info("Transactional steps compensated successfully for transactionalId: {}. Reverted status to: {}", transactionalId, transaction.getStatus());


    return true; // Return true if the compensation executed successfully, false otherwise
  }

  @Override
  public String getStepName() {
    return SagaStepType.UPDATE_TRANSACTION_WALLET_STEP.toString();
  }
}
