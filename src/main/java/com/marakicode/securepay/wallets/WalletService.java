package com.marakicode.securepay.wallets;

import com.marakicode.securepay.users.User;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@Service
public class WalletService {
    private final WalletRepository walletRepository;
    private PasswordEncoder encoder;

    public Wallet getWalletByUserId(Long userId) {
        return walletRepository.getWalletByUserId(userId)
                .orElseThrow(WalletNotFoundException::new);
    }

    public UserWalletDto getWalletDto(Long userId) {
        return new UserWalletDto(getWalletByUserId(userId).getBalance());
    }

    public void validatePin(User sender, String pin) {
        if (!encoder.matches(pin, sender.getPin())) {
            throw new InvalidPinException();
        }
    }

    public void validateSufficientBalance(
            Wallet senderWallet, BigDecimal amount) {
        if (senderWallet.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException();
        }
    }

    public void transfer(
            Wallet senderWallet, Wallet receiverWallet, BigDecimal amount) {
        senderWallet.setBalance(senderWallet.getBalance().subtract(amount));
        receiverWallet.setBalance(receiverWallet.getBalance().add(amount));
        walletRepository.saveAll(List.of(senderWallet, receiverWallet));
    }
}
