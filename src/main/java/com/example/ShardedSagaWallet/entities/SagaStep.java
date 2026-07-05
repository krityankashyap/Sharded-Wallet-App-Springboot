package com.example.ShardedSagaWallet.entities;

import org.springframework.data.annotation.Id;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "saga_step")
public class SagaStep {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @Column(name= "step_instance_id", nullable = false)
  private Long saga_instance_id;
  
  @Column(name= "step_name")
  private String stepName;
  
  @Column(name= "status", nullable = true)
  private String errorMessage;
  
  @Column(name= "step_data", nullable = true)
  private String step_data;
  
  @Column(name= "status", nullable = false)
  private SagaStepStatus status;
  

  public void markAsCompleted() {
    this.status = SagaStepStatus.COMPLETED;
  }

  public void markAsFailed() {
    this.status = SagaStepStatus.FAILED;
  }

  public void markAsInRunning() {
    this.status = SagaStepStatus.RUNNING;
  }

  public void markAsPending() {
    this.status = SagaStepStatus.PENDING;
  }

  public void markAsCompensating() {
    this.status = SagaStepStatus.COMPENSATING;
  }

  public void markAsCompensated() {
    this.status = SagaStepStatus.COMPENSATED;
  }
}
