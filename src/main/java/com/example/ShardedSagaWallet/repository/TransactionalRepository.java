package com.example.ShardedSagaWallet.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.ShardedSagaWallet.entities.Transaction;
import com.example.ShardedSagaWallet.entities.TransactionalStatus;

@Repository
public interface TransactionalRepository extends JpaRepository<Transaction , Long>{
 
  List<Transaction> findByToWalletId(Long toWalletId);

  List<Transaction> findByFromWalletId(Long fromWalletId);
  
  @Query("SELECT t FROM Transactional t WHERE t.fromWalletId = :walletId OR t.toWalletId = :walletId")
  List<Transaction> getAllTransactionals(@Param("walletId") Long walletId);

  List<Transaction> findBySagaInstanceId(String sagaInstanceId);

  List<Transaction> findByStatus(TransactionalStatus status);
}
