package com.example.ShardedSagaWallet.services;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ShardedSagaWallet.entities.Wallet;
import com.example.ShardedSagaWallet.repository.WalletRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class WalletService {

  private final WalletRepository walletRepository;

  public Wallet createWallet(Long userId){
    Wallet wallet= Wallet.builder()
      .userId(userId)
      .balance(BigDecimal.ZERO)
      .build();

      Wallet savedWallet= walletRepository.save(wallet);

      return savedWallet;
  }

  public Wallet getWalletById(Long id){
    return walletRepository.findById(id).orElseThrow(()-> new RuntimeException("Wallet not found with id: " + id));
  }

  public List<Wallet> getWalletByUserId(Long userId){
    return walletRepository.findByUserId(userId);
  }
  
  @Transactional
  public void debit(Long WalletId, BigDecimal amount){
    log.info("Debiting amount: {} from walletId: {}", amount, WalletId);
    Wallet wallet= getWalletById(WalletId);

    wallet.deductBalance(amount);
    walletRepository.save(wallet);
    log.info("Debited amount: {} from walletId: {}", amount, WalletId);
  }

  @Transactional
  public void credit(Long WalletId, BigDecimal amount){
    log.info("Crediting amount: {} to walletId: {}", amount, WalletId);
    Wallet wallet= getWalletById(WalletId);

    wallet.creditBalance(amount);
    walletRepository.save(wallet);
    log.info("Credited amount: {} to walletId: {}", amount, WalletId);
  }

  public BigDecimal getWalletBalance(Long walletId){
    Wallet wallet= getWalletById(walletId);
    return wallet.getBalance();
  }
}
