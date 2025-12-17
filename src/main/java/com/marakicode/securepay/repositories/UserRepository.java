package com.marakicode.securepay.repositories;

import com.marakicode.securepay.entities.User;
import com.marakicode.securepay.entities.Wallet;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    @EntityGraph(attributePaths = {"wallet"})
    @Query("select w from Wallet w where w.user.id = :userId")
    Optional<Wallet> getWalletByUserId(@Param("userId") Long userId);

}
