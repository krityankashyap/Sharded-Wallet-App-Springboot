package com.example.ShardedSagaWallet.services.saga;

import com.example.ShardedSagaWallet.repository.SagaInstanceRepository;
import com.example.ShardedSagaWallet.repository.SagaStepRepository;
import com.example.ShardedSagaWallet.services.saga.steps.SagaStepFactory;
import com.example.ShardedSagaWallet.services.saga.SagaStepInterface;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ShardedSagaWallet.entities.SagaInstance;
import com.example.ShardedSagaWallet.entities.SagaStatus;
import com.example.ShardedSagaWallet.entities.SagaStep;
import com.example.ShardedSagaWallet.entities.SagaStepStatus;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SagaOrchestratorImpl implements SagaOrchestrator {
  
  private final SagaInstanceRepository sagaInstanceRepository;
  private final ObjectMapper objectMapper;
  private final SagaStepFactory sagaStepFactory;
  private final SagaStepRepository sagaStepRepository;


  @Override
  @Transactional
  public Long startSaga(SagaContext context){
   try {
    // Convert the SagaContext to a JSON string
    String contextString= objectMapper.writeValueAsString(context);
    SagaInstance sagaInstance= SagaInstance.builder()
      .sagaContext(contextString)
      .status(SagaStatus.PENDING)
      .build();

      sagaInstance= sagaInstanceRepository.save(sagaInstance);

      log.info("Saga instance created with ID: {}", sagaInstance.getSagaId());

      return sagaInstance.getSagaId();

   } catch (Exception e) {
    log.error("Error starting saga: {}");
    throw new RuntimeException("Error starting saga", e);
   }
  } 
  
  @Override
  @Transactional
  public boolean executeStep(Long sagaInstanceId, String stepName){
    // find the saga instance by id
    SagaInstance sagaInstance= sagaInstanceRepository.findById(sagaInstanceId).orElseThrow(() -> new RuntimeException("Saga instance not found"));

    // Now we have to find the object sagaStep by getting a stepName
   SagaStepInterface step= sagaStepFactory.getSagaStep(stepName);

   if(step == null){
    log.error("Saga step not found for stepName: {}", stepName);
     throw new RuntimeException("Saga step not found for stepName: " + stepName);
   }
    SagaStep sagaStepDB = sagaStepRepository.findBySagaInstanceIdAndStatus(sagaInstanceId, SagaStepStatus.PENDING)
    .stream()
    .filter(s -> s.getStepName().equals(stepName))
    .findFirst()
    .orElse(SagaStep.builder().saga_instance_id(sagaInstanceId).stepName(stepName).status(SagaStepStatus.PENDING).build());

    if(sagaStepDB.getId() == null){  // if the sagaStep is new, we need to save it to the database
      sagaStepDB= sagaStepRepository.save(sagaStepDB);
    }
    
    try {
      log.info("Executing saga step: {} for sagaInstanceId: {}", stepName, sagaInstanceId);
      SagaContext sagaContext= objectMapper.readValue(sagaInstance.getSagaContext(), SagaContext.class);
      sagaStepDB.setStatus(SagaStepStatus.RUNNING);
      sagaStepDB= sagaStepRepository.save(sagaStepDB);
      boolean success= step.executeStep(sagaContext);

      if(success){
        sagaStepDB.setStatus(SagaStepStatus.COMPLETED);

        log.info("Saga step: {} executed successfully for sagaInstanceId: {}", stepName, sagaInstanceId);

        sagaInstance.setCurrStep(stepName);  // update the current step in the saga instance
        sagaInstance.setStatus(SagaStatus.RUNNING);

        return true;
      } else {
        sagaStepDB.setStatus(SagaStepStatus.FAILED);
        sagaStepRepository.save(sagaStepDB);
        log.error("Saga step: {} failed for sagaInstanceId: {}", stepName, sagaInstanceId);

        return false;
      }
    } catch (Exception e) {
      sagaStepDB.setStatus(SagaStepStatus.FAILED);
      sagaStepRepository.save(sagaStepDB);

      log.error("Error executing saga step: {} for sagaInstanceId: {}", stepName, sagaInstanceId, e);

      return false;
    }
  } 
  
  @Override
  @Transactional
  public boolean compensateStep(Long sagaInstanceId, String stepName){
      return false;
  } 
  
  @Override
  @Transactional
  public SagaInstance getSagaInstance(Long sagaInstanceId){
   return null;
  }
  
  @Override
  @Transactional
  public void compensateSaga(Long sagaInstanceId){
    
  }
  
  @Override
  @Transactional
  public void failSaga(Long sagaInstanceId){

  }
  
  @Override
  @Transactional
  public void completeSaga(Long sagaInstanceId){

  }
}
