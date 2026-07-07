package com.example.ShardedSagaWallet.services.saga;

import com.example.ShardedSagaWallet.repository.SagaInstanceRepository;
import com.example.ShardedSagaWallet.repository.SagaStepRepository;
import com.example.ShardedSagaWallet.services.saga.steps.SagaStepFactory;


import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ShardedSagaWallet.entities.SagaInstance;
import com.example.ShardedSagaWallet.entities.SagaStatus;
import com.example.ShardedSagaWallet.entities.SagaStep;
import com.example.ShardedSagaWallet.entities.SagaStepStatus;
import tools.jackson.databind.ObjectMapper;

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

      log.info("Saga instance created with ID: {}", sagaInstance.getId());

      return sagaInstance.getId();

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
    // SagaStep sagaStepDB = sagaStepRepository.findBySagaInstanceIdAndStatus(sagaInstanceId, SagaStepStatus.PENDING)
    // .stream()
    // .filter(s -> s.getStepName().equals(stepName))
    // .findFirst()
    // .orElse(SagaStep.builder().saga_instance_id(sagaInstanceId).stepName(stepName).status(SagaStepStatus.PENDING).build());

    SagaStep sagaStepDB = sagaStepRepository.findBySagaInstanceIdAndStepNameAndStatus(sagaInstanceId, stepName, SagaStepStatus.PENDING).orElseThrow(()-> new RuntimeException("Saga step not found for sagaInstanceId: " + sagaInstanceId + " and stepName: " + stepName));

    if(sagaStepDB.getId() == null){  // if the sagaStep is new, we need to save it to the database
      sagaStepDB= sagaStepRepository.save(sagaStepDB);
    }
    
    try {
      log.info("Executing saga step: {} for sagaInstanceId: {}", stepName, sagaInstanceId);
      SagaContext sagaContext= objectMapper.readValue(sagaInstance.getSagaContext(), SagaContext.class);
      sagaStepDB.markAsInRunning();
      sagaStepDB= sagaStepRepository.save(sagaStepDB);
      boolean success= step.executeStep(sagaContext);

      if(success){
        sagaStepDB.markAsCompleted();;

        log.info("Saga step: {} executed successfully for sagaInstanceId: {}", stepName, sagaInstanceId);

        sagaInstance.setCurrStep(stepName);  // update the current step in the saga instance
        sagaInstance.markAsInRunning();;
        sagaInstanceRepository.save(sagaInstance);

        return true;
      } else {
        sagaStepDB.markAsFailed();
        sagaStepRepository.save(sagaStepDB);
        log.error("Saga step: {} failed for sagaInstanceId: {}", stepName, sagaInstanceId);

        return false;
      }
    } catch (Exception e) {
      sagaStepDB.markAsFailed();
      sagaStepRepository.save(sagaStepDB);

      log.error("Error executing saga step: {} for sagaInstanceId: {}", stepName, sagaInstanceId, e);

      return false;
    }
  } 
  
  @Override
  @Transactional
  public boolean compensateStep(Long sagaInstanceId, String stepName){
       // find the saga instance by id
    SagaInstance sagaInstance= sagaInstanceRepository.findById(sagaInstanceId).orElseThrow(() -> new RuntimeException("Saga instance not found"));

    // Now we have to find the object sagaStep by getting a stepName
   SagaStepInterface step= sagaStepFactory.getSagaStep(stepName);

   if(step == null){
    log.error("Saga step not found for stepName: {}", stepName);
     throw new RuntimeException("Saga step not found for stepName: " + stepName);
   }
    // SagaStep sagaStepDB = sagaStepRepository.findBySagaInstanceIdAndStatus(sagaInstanceId, SagaStepStatus.PENDING)
    // .stream()
    // .filter(s -> s.getStepName().equals(stepName))
    // .findFirst()
    // .orElse(SagaStep.builder().saga_instance_id(sagaInstanceId).stepName(stepName).status(SagaStepStatus.PENDING).build());

    SagaStep sagaStepDB = sagaStepRepository.findBySagaInstanceIdAndStepNameAndStatus(sagaInstanceId, stepName, SagaStepStatus.COMPLETED).orElseThrow(null);

    if(sagaStepDB.getId() == null){  // if the sagaStep is new, we need to save it to the database
      log.info("No completed saga step found for compensation for sagaInstanceId: {} and stepName: {}", sagaInstanceId, stepName);
      return true;
    }
    
    try {
      log.info("Executing saga step: {} for sagaInstanceId: {}", stepName, sagaInstanceId);
      SagaContext sagaContext= objectMapper.readValue(sagaInstance.getSagaContext(), SagaContext.class);
      sagaStepDB.markAsCompensating();
      sagaStepDB= sagaStepRepository.save(sagaStepDB);
      boolean success= step.compensateSteps(sagaContext);

      if(success){
        sagaStepDB.markAsCompensated();;

        log.info("Saga step: {} compensated successfully ");

        sagaInstance.setCurrStep(stepName);  // update the current step in the saga instance

        return true;
      } else {
        sagaStepDB.markAsFailed();;
        sagaStepRepository.save(sagaStepDB);
        log.error("Saga step: {} failed for sagaInstanceId: {}", stepName, sagaInstanceId);

        return false;
      }
    } catch (Exception e) {
      sagaStepDB.markAsFailed();
      sagaStepRepository.save(sagaStepDB);

      log.error("Error in compensating saga ");

      return false;
    }
  } 
  
  @Override
  @Transactional
  public SagaInstance getSagaInstance(Long sagaInstanceId){
   return sagaInstanceRepository.findById(sagaInstanceId).orElseThrow(() -> new RuntimeException("Saga instance not found"));
  }
  
  @Override
  @Transactional
  public void compensateSaga(Long sagaInstanceId){

    SagaInstance sagaInstance= sagaInstanceRepository.findById(sagaInstanceId).orElseThrow(() -> new RuntimeException("Saga instance not found"));

    // mark the saga instance as compensating in db;
    sagaInstance.markAsCompensating();
    sagaInstanceRepository.save(sagaInstance);

    // get all the completed steps
    List<SagaStep> completedSteps= sagaStepRepository.findCompletedStepsBySagaInstanceId(sagaInstanceId);
    
    boolean allCompensated= true;  // flag to check if all steps are compensated successfully
    for(SagaStep sagaStep: completedSteps){
      boolean compensated= this.compensateStep(sagaInstanceId, sagaStep.getStepName());    
      
      if(!compensated){
        allCompensated= false;
        log.error("Failed to compensate saga step: {} for sagaInstanceId: {}", sagaStep.getStepName(), sagaInstanceId);
      }
    }

     if(allCompensated){
      sagaInstance.markAsCompensated();;
      sagaInstanceRepository.save(sagaInstance);
      log.info("All saga steps compensated successfully for sagaInstanceId: {}", sagaInstanceId);
     } else {
      sagaInstance.setStatus(SagaStatus.FAILED);
      sagaInstanceRepository.save(sagaInstance);
      log.error("Failed to compensate all saga steps for sagaInstanceId: {}", sagaInstanceId);
     }
  }
  
  @Override
  @Transactional
  public void failSaga(Long sagaInstanceId){
    SagaInstance sagaInstance= sagaInstanceRepository.findById(sagaInstanceId).orElseThrow(() -> new RuntimeException("Saga instance not found"));
    sagaInstance.markAsFailed();
    sagaInstanceRepository.save(sagaInstance);

    compensateSaga(sagaInstanceId);
  }
  
  @Override
  @Transactional
  public void completeSaga(Long sagaInstanceId){
    SagaInstance sagaInstance= sagaInstanceRepository.findById(sagaInstanceId).orElseThrow(() -> new RuntimeException("Saga instance not found"));
    sagaInstance.markAsCompleted();
    sagaInstanceRepository.save(sagaInstance);
  }
}
