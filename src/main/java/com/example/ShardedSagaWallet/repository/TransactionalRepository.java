package com.example.ShardedSagaWallet.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.ShardedSagaWallet.entities.Transactional;
import com.example.ShardedSagaWallet.entities.TransactionalStatus;

@Repository
public interface TransactionalRepository extends JpaRepository<Transactional , Long>{
 
  List<Transactional> findByToWalletId(Long toWalletId);

  List<Transactional> findByFromWalletId(Long fromWalletId);
  
  @Query("SELECT t FROM Transactional t WHERE t.fromWalletId = :walletId OR t.toWalletId = :walletId")
  List<Transactional> getAllTransactionals(@Param("walletId") Long walletId);

  List<Transactional> findBySagaInstanceId(String sagaInstanceId);

  List<Transactional> findByStatus(TransactionalStatus status);
}
