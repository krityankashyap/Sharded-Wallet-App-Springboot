package com.example.ShardedSagaWallet.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.example.ShardedSagaWallet.DTOs.CreateWalletDTO;
import com.example.ShardedSagaWallet.DTOs.CreditAmountDTO;
import com.example.ShardedSagaWallet.DTOs.DebitWalletDTO;
import com.example.ShardedSagaWallet.DTOs.TransactionRequestDTO;
import com.example.ShardedSagaWallet.DTOs.TransferResponseDTO;
import com.example.ShardedSagaWallet.entities.Transaction;
import com.example.ShardedSagaWallet.entities.Wallet;
import com.example.ShardedSagaWallet.services.TransactionService;
import com.example.ShardedSagaWallet.services.TransferSagaService;
import com.example.ShardedSagaWallet.services.WalletService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;




@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
@Slf4j
public class TransactionController {

  private final TransactionService transactionService;

  private final TransferSagaService transferSagaService;

  @PostMapping
  public ResponseEntity<TransferResponseDTO> createTransaction(@RequestBody TransactionRequestDTO transactionRequestDTO) {
    Long sagaInstanceId= transferSagaService.initiateTransfer(
      transactionRequestDTO.getToWalletId(),
      transactionRequestDTO.getFromWalletId(),
      transactionRequestDTO.getDescription(),
      transactionRequestDTO.getAmount()
    );

    return ResponseEntity.status(HttpStatus.CREATED).body(
      TransferResponseDTO.builder()
        .sagaInstanceId(sagaInstanceId)
        .build()
    );
  }

}
