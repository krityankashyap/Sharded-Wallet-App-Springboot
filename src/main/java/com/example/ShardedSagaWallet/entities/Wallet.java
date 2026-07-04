package com.example.ShardedSagaWallet.entities;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Builder
@Getter
@Setter
@Table(name = "wallet")
public class Wallet {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;
  
  @Column(name= "user_id", nullable = false)
  public Long userId;
  
  @Column(name= "is_active", nullable = false)
  private Boolean isActive;
  
  @Column(name= "balance", nullable = false)
  private BigDecimal balance;

  public boolean hasSufficientBalance(BigDecimal amount) {
    return balance.compareTo(amount) >= 0;
  }

  public void deductBalance(BigDecimal amount){
    if(!hasSufficientBalance(amount)){
      throw new IllegalArgumentException("Insufficient balance");
    }  else {
      balance = balance.subtract(amount);
    }
    }

  public void creditBalance(BigDecimal amount){
    balance = balance.add(amount);
  }
}

