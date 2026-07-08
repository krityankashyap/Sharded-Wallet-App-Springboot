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
      .isActive(true)
      .build();

      Wallet savedWallet= walletRepository.save(wallet);

      return savedWallet;
  }
  public Wallet getWalletById(Long id){
    return walletRepository.findById(id).orElseThrow(() -> new RuntimeException("Wallet not found with id: " + id));
  }

  public Wallet getWalletByUserId(Long userId){
    List<Wallet> wallets = walletRepository.findByUserId(userId);
    if (wallets.isEmpty()) {
        throw new RuntimeException("No wallet found for userId: " + userId);
    }
    return wallets.get(0);
  }
  
  @Transactional
  public void debit(Long userId, BigDecimal amount){
    log.info("Debiting amount: {} from walletId: {}", amount, userId);
    Wallet wallet= getWalletByUserId(userId);

    walletRepository.updateBalanceByUserId(userId, wallet.getBalance().subtract(amount));

    log.info("Debited amount: {} from walletId: {}", amount, userId);
  }

  @Transactional
  public void credit(Long userId, BigDecimal amount){
    log.info("Crediting amount: {} to walletId: {}", amount, userId);
    Wallet wallet= getWalletByUserId(userId);

   walletRepository.updateBalanceByUserId(userId, wallet.getBalance().add(amount));
    log.info("Credited amount: {} to walletId: {}", amount, userId);
  }

  public BigDecimal getWalletBalance(Long walletId){
    Wallet wallet= getWalletById(walletId);
    return wallet.getBalance();
  }
}
