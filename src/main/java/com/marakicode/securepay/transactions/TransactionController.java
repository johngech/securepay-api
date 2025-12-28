package com.marakicode.securepay.transactions;

import com.marakicode.securepay.common.ErrorDto;
import com.marakicode.securepay.wallets.InsufficientBalanceException;
import com.marakicode.securepay.wallets.InvalidPinException;
import com.marakicode.securepay.wallets.WalletNotFoundException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping
    public List<TransactionDto> getAllTransactions() {
        return transactionService.getAllTransactions();
    }

    @GetMapping("/{transactionCode}")
    public ResponseEntity<TransactionDto> getTransactionByTransactionCode(
            @PathVariable String transactionCode) {
        var transactionDto = transactionService.getTransactionByTransactionCode(transactionCode);
        return ResponseEntity.ok(transactionDto);
    }


    @GetMapping("/user/{userId}")
    public List<TransactionDto> getAllTransactionsByUser(@PathVariable Long userId) {
        return transactionService.getAllTransactionsByUserId(userId);
    }

    @GetMapping("/user/{userId}/sent")
    public List<TransactionDto> getSentTransactionsByUser(@PathVariable Long userId) {
        return transactionService.getSentTransactionByUserId(userId);
    }

    @GetMapping("/user/{userId}/received")
    public List<TransactionDto> getReceivedTransactionsByUser(@PathVariable Long userId) {
        return transactionService.getReceivedTransactionBySenderId(userId);
    }

    @GetMapping("/user/{userId}/{transactionCode}")
    public TransactionDto getTransactionForUserByTransactionCode(
            @PathVariable Long userId, @PathVariable String transactionCode) {
        return transactionService.getTransactionByUserIdAndTransactionCode(userId, transactionCode);
    }


    @DeleteMapping("/user/{userId}/{transactionCode}")
    public ResponseEntity<Void> deleteTransaction(
            @PathVariable Long userId, @PathVariable String transactionCode) {
        transactionService.deleteTransaction(userId, transactionCode);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(EmptyTransactionsException.class)
    public ResponseEntity<ErrorDto> handleEmptyTransaction() {
        return ResponseEntity.badRequest().body(new ErrorDto("Transaction not found"));
    }

    @ExceptionHandler(CannotSendToSameUserException.class)
    public ResponseEntity<ErrorDto> handleSame() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorDto("Cannot send to the same account"));
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ErrorDto> handleInsufficientBalance() {
        return ResponseEntity.badRequest()
                .body(new ErrorDto("Insufficient balance."));
    }

    @ExceptionHandler(InvalidPinException.class)
    public ResponseEntity<ErrorDto> handleInvalidPin() {
        return ResponseEntity.badRequest()
                .body(new ErrorDto("Invalid pin."));
    }

    @ExceptionHandler(WalletNotFoundException.class)
    public ResponseEntity<ErrorDto> handleUserWalletNotFound() {
        return ResponseEntity.badRequest().body(new ErrorDto("Wallet not found"));
    }

    @ExceptionHandler(TransactionNotFoundException.class)
    public ResponseEntity<ErrorDto> handleTransactionNotFound(Exception ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorDto("Transaction not found"));
    }
}
