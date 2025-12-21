package com.marakicode.securepay.wallets;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Integer> {
    @Query("select w from Wallet w where w.user.id = :userId")
    Optional<Wallet> getWalletByUserId(@Param("userId") Long userId);
}