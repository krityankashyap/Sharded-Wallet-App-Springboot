package com.example.ShardedSagaWallet.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.ShardedSagaWallet.entities.SagaStep;

@Repository
public interface SagaStepRepository extends JpaRepository<SagaStep, Long>{
 
  List<SagaStep> findBySagaInstanceId(Long sagaInstanceId);

  @Query("SELECT s FROM SagaStep s WHERE s.sagaInstanceId = :sagaInstanceId AND s.status = 'COMPLETED'")
  List<SagaStep> findCompletedStepsBySagaInstanceId(Long sagaInstanceId);

  @Query("SELECT s FROM SagaStep s WHERE s.sagaInstanceId = :sagaInstanceId AND s.status IN ('COMPLETED' , 'COMPENSATED')")
  List<SagaStep> findCompletedOrCompensatedStepsBySagaInstanceId(Long sagaInstanceId);
}
