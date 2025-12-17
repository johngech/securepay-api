package com.marakicode.securepay.controllers;

import com.marakicode.securepay.dtos.ChangePasswordRequest;
import com.marakicode.securepay.dtos.ChangePinRequest;
import com.marakicode.securepay.dtos.ErrorDto;
import com.marakicode.securepay.dtos.SendMoneyRequest;
import com.marakicode.securepay.dtos.TransactionDto;
import com.marakicode.securepay.dtos.UserDto;
import com.marakicode.securepay.dtos.UserRegisterRequest;
import com.marakicode.securepay.dtos.UserUpdateRequest;
import com.marakicode.securepay.dtos.UserWalletDto;
import com.marakicode.securepay.exceptions.EmailAlreadyExistException;
import com.marakicode.securepay.exceptions.EmptyTransactionsException;
import com.marakicode.securepay.exceptions.InsufficientBalanceException;
import com.marakicode.securepay.exceptions.InvalidPinException;
import com.marakicode.securepay.exceptions.PasswordMisMatchException;
import com.marakicode.securepay.exceptions.PhoneNumberAlreadyExistException;
import com.marakicode.securepay.exceptions.PinMisMatchException;
import com.marakicode.securepay.exceptions.SameAccountSendException;
import com.marakicode.securepay.exceptions.TransactionNotFoundException;
import com.marakicode.securepay.exceptions.UserNotFoundException;
import com.marakicode.securepay.exceptions.WalletNotFoundException;
import com.marakicode.securepay.services.TransactionService;
import com.marakicode.securepay.services.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final TransactionService transactionService;

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long userId) {
        var userDto = userService.getUserById(userId);
        return ResponseEntity.ok(userDto);
    }

    @PostMapping
    public ResponseEntity<UserDto> register(
            @Valid @RequestBody UserRegisterRequest request,
            UriComponentsBuilder builder) {
        var userDto = userService.registerUser(request);
        var uri = builder.path("/users/{userId}")
                .buildAndExpand(userDto.getId())
                .toUri();
        return ResponseEntity.created(uri).body(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserDto> update(
            @PathVariable Long userId,
            @Valid @RequestBody UserUpdateRequest request) {
        var userDto = userService.updateUser(userId, request);
        return ResponseEntity.ok(userDto);
    }

    // Action based update

    @PostMapping("/{userId}/change-pin")
    public ResponseEntity<Void> changePin(
            @PathVariable Long userId,
            @Valid @RequestBody ChangePinRequest request) {
        userService.changePin(userId, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{userId}/change-password")
    public ResponseEntity<Void> changePassword(
            @PathVariable Long userId,
            @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(userId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> delete(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}/wallet")
    public ResponseEntity<UserWalletDto> wallet(@PathVariable Long userId) {
        var userWallet = userService.wallet(userId);
        return ResponseEntity.ok(userWallet);
    }

    @PostMapping("/{senderId}/transactions")
    public ResponseEntity<TransactionDto> sendMoney(
            @PathVariable Long senderId, @Valid @RequestBody SendMoneyRequest request) {
        var transactionDto = transactionService.sendMoney(senderId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionDto);
    }

    @GetMapping("/{userId}/transactions")
    public List<TransactionDto> getAllTransactions(@PathVariable Long userId) {
        return transactionService.getAllTransactionsByUserId(userId);
    }

    @GetMapping("/{userId}/transactions/{transactionCode}")
    public TransactionDto getTransactionsByCode(
            @PathVariable Long userId, @PathVariable String transactionCode) {
        return transactionService.getTransactionByUserIdAndTransactionCode(userId, transactionCode);
    }

    @DeleteMapping("/{userId}/transactions/{transactionCode}")
    public ResponseEntity<Void> deleteTransaction(
            @PathVariable Long userId,@PathVariable String transactionCode){
        transactionService.deleteTransaction(userId,transactionCode);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}/transactions/sent")
    public List<TransactionDto> sentTransactions(@PathVariable Long userId) {
        return transactionService.getSentTransactionByUserId(userId);
    }

    @GetMapping("/{senderId}/transactions/received")
    public List<TransactionDto> receivedTransaction(@PathVariable Long senderId) {
        return transactionService.getReceivedTransactionBySenderId(senderId);
    }

    @ExceptionHandler(EmailAlreadyExistException.class)
    public ResponseEntity<ErrorDto> handleEmailAlreadyExist() {
        return ResponseEntity.badRequest()
                .body(new ErrorDto("Email already exist."));
    }

    @ExceptionHandler(PhoneNumberAlreadyExistException.class)
    public ResponseEntity<ErrorDto> handlePhoneExist() {
        return ResponseEntity.badRequest()
                .body(new ErrorDto("Phone number already exist"));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorDto> handleUserNotFound() {
        return ResponseEntity.badRequest().body(new ErrorDto("User not found"));
    }

    @ExceptionHandler(PasswordMisMatchException.class)
    public ResponseEntity<ErrorDto> handlePasswordMisMatch() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorDto("Invalid password"));
    }

    @ExceptionHandler(PinMisMatchException.class)
    public ResponseEntity<ErrorDto> handlePinMisMatch() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorDto("Invalid Pin"));
    }

    @ExceptionHandler(WalletNotFoundException.class)
    public ResponseEntity<ErrorDto> handleUserWalletNotFound() {
        return ResponseEntity.badRequest().body(new ErrorDto("Wallet not found"));
    }

    @ExceptionHandler(EmptyTransactionsException.class)
    public ResponseEntity<ErrorDto> handleEmptyTransaction() {
        return ResponseEntity.badRequest().body(new ErrorDto("Transaction not found"));
    }

    @ExceptionHandler(SameAccountSendException.class)
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

    @ExceptionHandler(TransactionNotFoundException.class)
    public ResponseEntity<ErrorDto> handleTransactionNotFound(Exception ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorDto("Transaction not found"));
    }
}
