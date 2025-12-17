package com.marakicode.securepay.repositories;

import com.marakicode.securepay.entities.Transaction;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @EntityGraph(attributePaths = "provider")
    @Override
    List<Transaction> findAll();

    @EntityGraph(attributePaths = "provider")
    Optional<Transaction> findByTransactionCode(String transactionCode);

    @EntityGraph(attributePaths = {"participants","provider"})
    @Query(""" 
        SELECT DISTINCT t from Transaction as t
        JOIN t.participants as p
        ON p.sender.id = :userId
    """)
    List<Transaction> getSentTransactionByUserId(@Param("userId") Long userId);

    @EntityGraph(attributePaths = {"participants","provider"})
    @Query(""" 
        SELECT DISTINCT t from Transaction as t
        JOIN t.participants as p
        ON p.receiver.id = :userId
    """)
    List<Transaction> getReceivedTransactionBySenderId(@Param("userId") Long userId);

    @EntityGraph(attributePaths = {"participants","provider"})
    @Query(""" 
                SELECT DISTINCT t from Transaction as t
                JOIN t.participants as p
                WHERE p.receiver.id = :userId OR p.sender.id = :userId
                ORDER BY t.createdAt DESC
            """)
    List<Transaction> getAllTransactionsByUserId(@Param("userId") Long userId);

    @EntityGraph(attributePaths = {"participants","provider"})
    @Query(""" 
                SELECT DISTINCT t from Transaction as t
                JOIN t.participants as p
                WHERE (p.receiver.id = :userId OR p.sender.id = :userId) AND t.transactionCode = :transactionCode
                ORDER BY t.createdAt DESC
            """)
    Optional<Transaction> getTransactionByUserIdAndTransactionCode(
            @Param("userId") Long userId, @Param("transactionCode") String transactionCode);

}
