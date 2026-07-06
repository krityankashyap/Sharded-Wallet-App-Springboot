package com.example.ShardedSagaWallet.services;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ShardedSagaWallet.entities.Transaction;
import com.example.ShardedSagaWallet.entities.TransactionalStatus;
import com.example.ShardedSagaWallet.repository.TransactionalRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionService {

private final TransactionalRepository transactionalRepository;

@Transactional
public Transaction createTransaction(Long fromWalletId, Long toWalletId, BigDecimal amount, String discription) {
   Transaction transaction= Transaction.builder()
     .fromWalletId(fromWalletId)
     .toWalletId(toWalletId)
     .amount(amount)
     .discription(discription)
     .build();

     Transaction savedTransaction= transactionalRepository.save(transaction);
     log.info("Transaction created with id: {}", savedTransaction.getId());

     return savedTransaction;

}
public Transaction geTransactionById(Long id){
  return transactionalRepository.findById(id).orElseThrow(()-> new RuntimeException("Transaction not found with id: " + id));
}

public List<Transaction> getAllTransactionals(Long walletId){
  return transactionalRepository.getAllTransactionals(walletId);

}

public List<Transaction> getTransactionsToWalletId(Long toWalletId){
  return transactionalRepository.findByToWalletId(toWalletId);
}

public List<Transaction> geTransactionsFromWalletId(Long fromWalletId){
  return transactionalRepository.findByFromWalletId(fromWalletId);
}

public List<Transaction> getTransactionsBySagaInstanceId(String sagaInstanceId){
  return transactionalRepository.findBySagaInstanceId(sagaInstanceId);
}

public List<Transaction> getTransactionsByStatus(TransactionalStatus status){
  return transactionalRepository.findByStatus(status);
}

public void updateTransactionWithSagaInstanceId(Long transactionId, Long sagaInstanceId){
Transaction transaction= geTransactionById(transactionId);
transaction.setSagaInstanceId(sagaInstanceId);
transactionalRepository.save(transaction);
log.info("Transaction with id: {} updated with sagaInstanceId: {}", transactionId, sagaInstanceId);
}

}
