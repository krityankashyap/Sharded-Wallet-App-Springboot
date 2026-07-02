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
  
  @Column(name= "step_name", nullable = false)
  private String stepName;
  
  @Column(name= "status", nullable = true)
  private String errorMessage;
  
  @Column(name= "step_data", nullable = true)
  private String step_data;
  
  @Column(name= "status", nullable = false)
  private SagaStepStatus status;

}
