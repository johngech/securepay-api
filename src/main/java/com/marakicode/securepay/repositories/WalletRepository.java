package com.marakicode.securepay.repositories;

import com.marakicode.securepay.entities.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Integer> {
    @Query("select w from Wallet w where w.user.id = :userId")
    Optional<Wallet> getWalletByUser(@Param("userId") Long userId);
}