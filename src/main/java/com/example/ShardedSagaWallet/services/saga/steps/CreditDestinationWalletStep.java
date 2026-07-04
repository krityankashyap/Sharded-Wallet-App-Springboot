package com.example.ShardedSagaWallet.services.saga.steps;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ShardedSagaWallet.entities.Wallet;
import com.example.ShardedSagaWallet.repository.WalletRepository;
import com.example.ShardedSagaWallet.services.saga.SagaContext;
import com.example.ShardedSagaWallet.services.saga.SagaStepInterface;
import com.example.ShardedSagaWallet.services.saga.steps.SagaStepFactory.SagaStepType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreditDestinationWalletStep implements SagaStepInterface{

  private final WalletRepository walletRepository;

  @Override
  @Transactional
  public boolean executeStep(SagaContext context) {
    log.info("Executing CreditDestinationWalletStep for sagaId: {}");
    //  Step1:- Get the destination wallet id from the context
    Long towalletId= context.getLong("toWalletId");
    BigDecimal amount= context.getBigDecimal("amount");

    log.info("Crediting destination wallet with ID: {} with amount: {}", towalletId, amount);


    // Step2:- fetch the destination wallet from the database with a lock
    Wallet destinationWallet= walletRepository.findById(towalletId).orElseThrow(()-> new RuntimeException("Destination wallet not found"));
    log.info("Wallet fetched with balance: {}", destinationWallet.getBalance());
    context.put("orginalToWalletBalance", destinationWallet.getBalance()); // store the original balance in the context for compensation if needed


    // Step3:- credit the destination wallet
    destinationWallet.creditBalance(amount);
    walletRepository.save(destinationWallet);
    log.info("Destination wallet credited successfully. New balance: {}", destinationWallet.getBalance()); // New balance after crediting
    context.put("toWalletBalanceAfterCredit", destinationWallet.getBalance()); // store the new balance in the context for reference

    // Step4:- update the context with the new balance or any other relevant information
    log.info("CreditDestinationWalletStep executed successfully");
    return true;
  }

  @Override
  @Transactional
  public boolean compensateSteps(SagaContext context) {  // This method is called if the saga fails and we need to roll back the changes made by this step
    log.info("Compensating CreditDestinationWalletStep for sagaId: {}");
    //  Step1:- Get the destination wallet id from the context
    Long towalletId= context.getLong("toWalletId");
    BigDecimal amount= context.getBigDecimal("amount");

    log.info("Compensating credit of destination wallet with ID: {} with amount: {}", towalletId, amount);


    // Step2:- fetch the destination wallet from the database with a lock
    Wallet destinationWallet= walletRepository.findById(towalletId).orElseThrow(()-> new RuntimeException("Destination wallet not found"));
    log.info("Wallet fetched with balance: {}", destinationWallet.getBalance());
   
    // Step3:- credit the destination wallet
    destinationWallet.deductBalance(amount);
    walletRepository.save(destinationWallet);
    log.info("Destination wallet credited successfully. New balance: {}", destinationWallet.getBalance()); // New balance after crediting
    context.put("toWalletBalanceAfterCredit", destinationWallet.getBalance()); // store the new balance in the context for reference

    // Step4:- update the context with the new balance or any other relevant information
    log.info("Credit compensating the Destination Wallet executed successfully");
    return true;
  }

  @Override
  public String getStepName() {
    return SagaStepType.CREDIT_DESTINATION_WALLET_STEP.toString();
  }
}
