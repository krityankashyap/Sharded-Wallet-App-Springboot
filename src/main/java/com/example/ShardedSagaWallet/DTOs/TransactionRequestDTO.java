package com.example.ShardedSagaWallet.DTOs;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequestDTO {

 public Long toWalletId; // toUserId
 
  public Long fromWalletId; // fromUserId

  public BigDecimal amount;

  public String description;
}
