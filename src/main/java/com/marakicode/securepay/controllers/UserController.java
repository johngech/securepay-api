package com.marakicode.securepay.controllers;

import com.marakicode.securepay.dtos.ChangePasswordRequest;
import com.marakicode.securepay.dtos.ChangePinRequest;
import com.marakicode.securepay.dtos.ErrorDto;
import com.marakicode.securepay.dtos.UserDto;
import com.marakicode.securepay.dtos.UserRegisterRequest;
import com.marakicode.securepay.dtos.UserUpdateRequest;
import com.marakicode.securepay.exceptions.EmailAlreadyExistException;
import com.marakicode.securepay.exceptions.PasswordMisMatchException;
import com.marakicode.securepay.exceptions.PhoneNumberAlreadyExistException;
import com.marakicode.securepay.exceptions.PinMisMatchException;
import com.marakicode.securepay.exceptions.UserNotFoundException;
import com.marakicode.securepay.mappers.UserMapper;
import com.marakicode.securepay.repositories.UserRepository;
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

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserService userService;

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

}
