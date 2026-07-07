package com.example.ShardedSagaWallet.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
  private Long sagaInstanceId;
  
  @Column(name= "step_name")
  private String stepName;
  
  @Column(name= "error_message", nullable = true)
  private String errorMessage;
  
  @Column(name= "step_data", nullable = true)
  private String step_data;
  
  @Enumerated(EnumType.STRING)
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
