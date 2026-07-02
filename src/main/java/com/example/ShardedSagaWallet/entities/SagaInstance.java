package com.example.ShardedSagaWallet.entities;

import groovy.transform.builder.Builder;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "saga_instance")
public class SagaInstance {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private String sagaId;
  
  @Enumerated(EnumType.STRING)
  @Column(name= "status", nullable = false)
  private SagaStatus status= SagaStatus.STARTED;
  
  @Column(name= "saga_context", columnDefinition="TEXT", nullable = false)
  private String sagaContext;
  
  @Column(name= "curr_step", nullable = false)
  private String currStep;
}
