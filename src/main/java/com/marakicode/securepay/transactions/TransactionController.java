package com.marakicode.securepay.transactions;

import com.marakicode.securepay.common.ErrorDto;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @ExceptionHandler(TransactionNotFoundException.class)
    public ResponseEntity<ErrorDto> handleTransactionNotFound(Exception ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorDto("Transaction not found"));
    }
}
