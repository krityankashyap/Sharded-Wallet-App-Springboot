package com.example.ShardedSagaWallet.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.example.ShardedSagaWallet.DTOs.CreateWalletDTO;
import com.example.ShardedSagaWallet.DTOs.CreditAmountDTO;
import com.example.ShardedSagaWallet.DTOs.DebitWalletDTO;
import com.example.ShardedSagaWallet.entities.Wallet;
import com.example.ShardedSagaWallet.services.WalletService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;




@RestController
@RequestMapping("/wallets")
@RequiredArgsConstructor
@Slf4j
public class WalletController {

  private final WalletService walletService;
  
  @PostMapping
  public ResponseEntity<Wallet> createWallet(@RequestBody CreateWalletDTO createWalletDTO) {
    Wallet wallet= walletService.createWallet(createWalletDTO.getUserId());
    return new ResponseEntity<>(wallet, HttpStatus.CREATED);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Wallet> getWalletById(@PathVariable Long id){
      Wallet wallet = walletService.getWalletById(id);
      return new ResponseEntity<>(wallet, HttpStatus.OK);
  }

  @GetMapping("/{id}/balance")
  public ResponseEntity<String> getWalletBalance(@PathVariable Long id){
      Wallet wallet = walletService.getWalletById(id);
      return new ResponseEntity<>("Balance: " + wallet.getBalance(), HttpStatus.OK);
  }

  @PostMapping("/{userId}/debit")
  public ResponseEntity<Wallet> debitWallet(@PathVariable Long userId, @RequestBody DebitWalletDTO debitWalletDTO){
    walletService.debit(userId, debitWalletDTO.getAmount());
    Wallet wallet = walletService.getWalletByUserId(userId);
    return new ResponseEntity<>(wallet, HttpStatus.OK);
  }

  @PostMapping("/{userId}/credit")
  public ResponseEntity<Wallet> creditWallet(@PathVariable Long userId, @RequestBody CreditAmountDTO creditWalletDTO) {
    walletService.credit(userId, creditWalletDTO.getAmount());
    Wallet wallet = walletService.getWalletByUserId(userId);
    return new ResponseEntity<>(wallet, HttpStatus.OK);
  }

  

}
