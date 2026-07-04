package com.example.ShardedSagaWallet.services.saga.steps;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ShardedSagaWallet.entities.Wallet;
import com.example.ShardedSagaWallet.repository.WalletRepository;
import com.example.ShardedSagaWallet.services.saga.SagaContext;
import com.example.ShardedSagaWallet.services.saga.SagaStep;
import com.example.ShardedSagaWallet.services.saga.steps.SagaStepFactory.SagaStepType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class DebitSourceWalletStep implements SagaStep {

  private final WalletRepository walletRepository;

  @Override
  @Transactional
  public boolean executeStep(SagaContext context) {
    Long fromWalletId= context.getLong("fromWalletId");
    BigDecimal amount= context.getBigDecimal("amount");

    log.info("Debiting source wallet with ID: {} with amount: {}", fromWalletId, amount);

    Wallet sourceWallet= walletRepository.findByIdWithLock(fromWalletId).orElseThrow(() -> new RuntimeException("Source wallet not found"));

    log.info("Source wallet fetched with balance: {}", sourceWallet.getBalance());
    context.put("originalFromWalletBalance", sourceWallet.getBalance()); // store the original balance in the context for compensation if needed

    if(!sourceWallet.hasSufficientBalance(amount)){
      throw new RuntimeException("Insufficient balance in source wallet");
    }

    sourceWallet.deductBalance(amount);
    walletRepository.save(sourceWallet);

    log.info("Source wallet debited successfully. New balance: {}", sourceWallet.getBalance()); // New balance after debiting
    context.put("fromWalletBalanceAfterDebit", sourceWallet.getBalance()); // store the new balance in the context for reference

    return true; // Return true if successful, false otherwise
  }

  @Override
  @Transactional
  public boolean compensateSteps(SagaContext context) {
    Long fromWalletId= context.getLong("fromWalletId");
    BigDecimal amount= context.getBigDecimal("amount");

    log.info("Crediting compensating source wallet with ID: {} with amount: {}", fromWalletId, amount);

    Wallet sourceWallet= walletRepository.findByIdWithLock(fromWalletId).orElseThrow(() -> new RuntimeException("Source wallet not found"));

    log.info("Source wallet fetched with balance: {}", sourceWallet.getBalance());

    if(!sourceWallet.hasSufficientBalance(amount)){
      throw new RuntimeException("Insufficient balance in source wallet");
    }

    sourceWallet.creditBalance(amount);
    walletRepository.save(sourceWallet);

    log.info("Source wallet compensated successfully. New balance: {}", sourceWallet.getBalance()); // New balance after debiting
    
    return true; // Return true if successful, false otherwise
  }

  @Override
  public String getStepName() {
    return SagaStepType.DEBIT_SOURCE_WALLET_STEP.toString();
  }
}
