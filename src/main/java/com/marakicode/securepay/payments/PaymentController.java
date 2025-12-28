package com.marakicode.securepay.payments;

import com.marakicode.securepay.auth.AuthUser;
import com.marakicode.securepay.common.ErrorDto;
import com.marakicode.securepay.transactions.SendMoneyRequest;
import com.marakicode.securepay.transactions.TransactionDto;
import com.marakicode.securepay.users.UserNotFoundException;
import com.marakicode.securepay.wallets.InsufficientBalanceException;
import com.marakicode.securepay.wallets.InvalidPinException;
import com.marakicode.securepay.wallets.WalletNotFoundException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/top-up")
    public PaymentSessionResponse topUp(
            @AuthenticationPrincipal AuthUser user,
            @Valid @RequestBody DepositRequest request) {
        return paymentService.createPaymentIntent(user.id(), request.amount());
    }

    @PostMapping("/webhook")
    public void handleWebhook(
            @RequestHeader Map<String, String> headers,
            @RequestBody String payload) {
        paymentService.handleWebhookRequest(new WebhookRequest(headers, payload));
    }

    @PostMapping("/send")
    public ResponseEntity<TransactionDto> send(
            @AuthenticationPrincipal AuthUser user,
            @Valid @RequestBody SendMoneyRequest request) {
        var transactionDto = paymentService.sendMoney(user.id(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionDto);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorDto> handleUserNotFound(Exception ex) {
        return ResponseEntity.badRequest().body(new ErrorDto(ex.getMessage()));
    }

    @ExceptionHandler(InvalidPinException.class)
    public ResponseEntity<ErrorDto> invalidPin(Exception ex) {
        return ResponseEntity.badRequest().body(new ErrorDto(ex.getMessage()));
    }

    @ExceptionHandler(WalletNotFoundException.class)
    public ResponseEntity<ErrorDto> handleWalletNotFound(Exception ex) {
        return ResponseEntity.badRequest().body(new ErrorDto(ex.getMessage()));
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ErrorDto> handleInsufficientBalance() {
        return ResponseEntity.badRequest().body(new ErrorDto("Insufficient balance"));
    }

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<ErrorDto> handlePaymentException(Exception ex) {
        return ResponseEntity.badRequest().body(new ErrorDto(ex.getMessage()));
    }
}
