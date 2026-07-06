package com.example.ShardedSagaWallet.services;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ShardedSagaWallet.entities.Transaction;
import com.example.ShardedSagaWallet.services.saga.SagaContext;
import com.example.ShardedSagaWallet.services.saga.SagaOrchestrator;
import com.example.ShardedSagaWallet.services.saga.steps.SagaStepFactory.*;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransferSagaService {
  
  private final TransactionService transactionService;
  private final SagaOrchestrator sagaOrchestrator;

  @Transactional
  public Long initiateTransfer(Long fromWalletId, Long toWalletId, String discription, BigDecimal amount){

    log.info("Initiating transfer from walletId: {} to walletId: {} with amount: {}", fromWalletId, toWalletId, amount);

    // 1) create transaction and put it inside sagacontext

    Transaction transaction= transactionService.createTransaction(fromWalletId, toWalletId, amount, discription);

    SagaContext sagaContext= SagaContext.builder()
                             .data(Map.ofEntries(
                                Map.entry("transactionId", transaction.getId()),
                                Map.entry("fromWalletId", fromWalletId),
                                Map.entry("toWalletId", toWalletId),
                                Map.entry("amount", amount),
                                Map.entry("discription", discription)
                             ))
                             .build();

    Long sagaInstanceId= sagaOrchestrator.startSaga(sagaContext);   // create the saga and return the sagaInstanceId                       
    log.info("Saga instance created with id: {}", sagaInstanceId);

    transactionService.updateTransactionWithSagaInstanceId(transaction.getId(), sagaInstanceId); // update the transaction with sagaInstanceId

    executeTransferSaga(sagaInstanceId); // execute the saga

    return sagaInstanceId;
  } 
  
  public void executeTransferSaga(Long sagaInstanceId){
   log.info("Executing transfer saga with sagaInstanceId: {}", sagaInstanceId);

   try {
    for(SagaStepType sagaStep: SagaStepFactory.TransferSagaSteps){


      log.info("Executing saga step: {} for sagaInstanceId: {}", sagaStep, sagaInstanceId);
      boolean success= sagaOrchestrator.executeStep(sagaInstanceId, sagaStep.toString());
      
      if(!success){
        log.info(null, "Saga step: {} failed for sagaInstanceId: {}", sagaStep, sagaInstanceId);
        sagaOrchestrator.failSaga(sagaInstanceId);
      }
    }
    log.info("All saga steps executed successfully for sagaInstanceId: {}", sagaInstanceId);
    sagaOrchestrator.completeSaga(sagaInstanceId);

   } catch (Exception e) {
    log.error(null, "Exception occurred while executing saga steps for sagaInstanceId: {}", sagaInstanceId, e);
    sagaOrchestrator.failSaga(sagaInstanceId);
   }
  }

}
