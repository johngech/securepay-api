package com.marakicode.securepay.services;

import com.marakicode.securepay.dtos.ChangePasswordRequest;
import com.marakicode.securepay.dtos.ChangePinRequest;
import com.marakicode.securepay.dtos.UserDto;
import com.marakicode.securepay.dtos.UserRegisterRequest;
import com.marakicode.securepay.dtos.UserUpdateRequest;
import com.marakicode.securepay.dtos.UserWalletDto;
import com.marakicode.securepay.entities.User;
import com.marakicode.securepay.entities.Wallet;
import com.marakicode.securepay.exceptions.EmailAlreadyExistException;
import com.marakicode.securepay.exceptions.PasswordMisMatchException;
import com.marakicode.securepay.exceptions.PhoneNumberAlreadyExistException;
import com.marakicode.securepay.exceptions.PinMisMatchException;
import com.marakicode.securepay.exceptions.UserNotFoundException;
import com.marakicode.securepay.mappers.UserMapper;
import com.marakicode.securepay.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder encoder;
    private final WalletService walletService;

    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    public User getUserEntity(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
    }

    public UserDto getUserById(Long userId) {
        return userMapper.toDto(getUserEntity(userId));
    }

    public UserWalletDto getUserWallet(Long userId) {
        return walletService.getWalletDto(userId);
    }

    public UserDto registerUser(UserRegisterRequest request) {
        validateRegistration(request);

        var user = userMapper.toEntity(request);
        user.setPassword(encoder.encode(request.password()));

        // Create a wallet for user
        var wallet = new Wallet();
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setUser(user);

        userRepository.save(user);
        return userMapper.toDto(user);
    }

    public UserDto updateUser(Long userId, UserUpdateRequest request) {
        var user = getUserEntity(userId);
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        userRepository.save(user);

        return userMapper.toDto(user);
    }

    public void deleteUser(Long userId) {
        userRepository.delete(getUserEntity(userId));
    }

    public void changePin(Long userId, ChangePinRequest request) {
        var user = getUserEntity(userId);

        if (!encoder.matches(request.oldPin(), user.getPin())) {
            throw new PinMisMatchException();
        }

        user.setPin(encoder.encode(request.newPin()));
        userRepository.save(user);
    }

    public void changePassword(Long userId, ChangePasswordRequest request) {
        var user = getUserEntity(userId);

        if (!encoder.matches(request.oldPassword(), user.getPassword()))
            throw new PasswordMisMatchException();

        user.setPassword(encoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    private void validateRegistration(UserRegisterRequest request) {
        if (userRepository.existsByEmail(request.email()))
            throw new EmailAlreadyExistException();

        if (userRepository.existsByPhone(request.phone()))
            throw new PhoneNumberAlreadyExistException();
    }
}
