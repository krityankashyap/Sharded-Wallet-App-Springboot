package com.example.ShardedSagaWallet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ShardedSagaWallet.entities.SagaInstance;

@Repository
public interface SagaInstanceRepository extends JpaRepository<SagaInstance, Long>{

}
