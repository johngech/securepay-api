package com.marakicode.securepay.services;

import com.marakicode.securepay.entities.Wallet;
import com.marakicode.securepay.exceptions.InsufficientBalanceException;
import com.marakicode.securepay.exceptions.WalletNotFoundException;
import com.marakicode.securepay.repositories.WalletRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@Service
public class WalletService {
    private final WalletRepository walletRepository;

    public Wallet getWallet(Long userId) {
        return walletRepository.getWalletByUser(userId)
                .orElseThrow(WalletNotFoundException::new);
    }

    public void validateSufficientBalance(
            Wallet senderWallet, BigDecimal amount) {
        if (senderWallet.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException();
        }
    }

    public void manageTransfer(
            Wallet senderWallet, Wallet receiverWallet, BigDecimal amount) {
        senderWallet.setBalance(senderWallet.getBalance().subtract(amount));
        receiverWallet.setBalance(receiverWallet.getBalance().add(amount));
        walletRepository.saveAll(List.of(senderWallet, receiverWallet));
    }
}
