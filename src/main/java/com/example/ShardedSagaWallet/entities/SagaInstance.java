package com.example.ShardedSagaWallet.entities;


import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
  @Column(name = "id")
  private Long id;
  
  @Enumerated(EnumType.STRING)
  @Column(name= "status", nullable = false)
  private SagaStatus status;
  
  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name= "saga_context", columnDefinition="json")
  private String sagaContext;
  
  @Column(name= "curr_step", nullable = false)
  private String currStep;

  public void markAsCompleted() {
    this.status = SagaStatus.COMPLETED;
  }

  public void markAsFailed() {
    this.status = SagaStatus.FAILED;
  }

  public void markAsInRunning() {
    this.status = SagaStatus.RUNNING;
  }

  public void markAsPending() {
    this.status = SagaStatus.PENDING;
  }

  public void markAsCompensating() {
    this.status = SagaStatus.COMPENSATING;
  }

  public void markAsCompensated() {
    this.status = SagaStatus.COMPENSATED;
  }
}
