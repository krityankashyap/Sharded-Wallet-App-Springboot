package com.example.ShardedSagaWallet.entities;

import java.math.BigDecimal;

import org.springframework.data.annotation.Id;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "transactional")
public class Transactional {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name="from_wallet_id", nullable = false)
  private Long fromWalletId;
  
  @Column(name="to_wallet_id", nullable = false)
  private Long toWalletId;
  
  @Column(name="amount", nullable = false)
  private BigDecimal amount;
  
  @Enumerated(EnumType.STRING)
  @Column(name="status", nullable = false)
  private TransactionalStatus status = TransactionalStatus.PENDING;
  

  @Enumerated(EnumType.STRING)
  @Column(name="transactional_type", nullable = false)
  private TransactionalType type= TransactionalType.TRANSFER;
  

  @Column(name="discription", nullable = false)
  private String discription;
  
  @Column(name="saga_instance_id", nullable = false)
  private String sagaInstanceId;

}
