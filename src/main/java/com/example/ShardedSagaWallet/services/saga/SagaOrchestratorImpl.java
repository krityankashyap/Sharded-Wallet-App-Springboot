package com.example.ShardedSagaWallet.services.saga;

import com.example.ShardedSagaWallet.repository.SagaInstanceRepository;
import com.example.ShardedSagaWallet.services.saga.steps.SagaStepFactory;
import com.example.ShardedSagaWallet.entities.SagaStep;
import org.springframework.stereotype.Service;

import com.example.ShardedSagaWallet.entities.SagaInstance;
import com.example.ShardedSagaWallet.entities.SagaStatus;
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



  public Long startSaga(SagaContext context){
   try {
    // Convert the SagaContext to a JSON string
    String contextString= objectMapper.writeValueAsString(context);
    SagaInstance sagaInstance= SagaInstance.builder()
      .sagaContext(contextString)
      .status(SagaStatus.STARTED)
      .build();

      sagaInstance= sagaInstanceRepository.save(sagaInstance);

      log.info("Saga instance created with ID: {}", sagaInstance.getSagaId());

      return sagaInstance.getSagaId();

   } catch (Exception e) {
    log.error("Error starting saga: {}");
    throw new RuntimeException("Error starting saga", e);
   }
  } 

  public boolean executeStep(Long sagaInstanceId, String stepName){
    // find the saga instance by id
    SagaInstance sagaInstance= sagaInstanceRepository.findById(sagaInstanceId).orElseThrow(() -> new RuntimeException("Saga instance not found"));

    // Now we have to find the object sagaStep by getting a stepName
   SagaStep sagaStep= sagaStepFactory.getSagaStep(stepName);
    return false;
  } 

  public boolean compensateStep(Long sagaInstanceId, String stepName){
      return false;
  } 
  
  public SagaInstance getSagaInstance(Long sagaInstanceId){
   return null;
  }

  public void compensateSaga(Long sagaInstanceId){
    
  }

  public void failSaga(Long sagaInstanceId){

  }

  public void completeSaga(Long sagaInstanceId){

  }
}
