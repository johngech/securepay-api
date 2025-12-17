package com.marakicode.securepay.services;

import com.marakicode.securepay.dtos.ChangePasswordRequest;
import com.marakicode.securepay.dtos.ChangePinRequest;
import com.marakicode.securepay.dtos.UserDto;
import com.marakicode.securepay.dtos.UserRegisterRequest;
import com.marakicode.securepay.dtos.UserUpdateRequest;
import com.marakicode.securepay.dtos.UserWalletDto;
import com.marakicode.securepay.entities.Wallet;
import com.marakicode.securepay.exceptions.EmailAlreadyExistException;
import com.marakicode.securepay.exceptions.PasswordMisMatchException;
import com.marakicode.securepay.exceptions.PhoneNumberAlreadyExistException;
import com.marakicode.securepay.exceptions.PinMisMatchException;
import com.marakicode.securepay.exceptions.UserNotFoundException;
import com.marakicode.securepay.exceptions.WalletNotFoundException;
import com.marakicode.securepay.mappers.UserMapper;
import com.marakicode.securepay.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    public UserDto getUserById(Long userId) {
        var user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new UserNotFoundException();
        }
        return userMapper.toDto(user);
    }

    public UserDto registerUser(UserRegisterRequest request) {
        if (userRepository.existsByEmail(request.email()))
            throw new EmailAlreadyExistException();

        if (userRepository.existsByPhone(request.phone()))
            throw new PhoneNumberAlreadyExistException();

        var user = userMapper.toEntity(request);
        // Hash password here

        // Create a wallet for user
        var wallet = new Wallet();
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setUser(user);

        userRepository.save(user);
        return userMapper.toDto(user);
    }

    public UserDto updateUser(Long userId, UserUpdateRequest request) {
        var user = userRepository.findById(userId).orElse(null);
        if (user == null)
            throw new UserNotFoundException();

        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        userRepository.save(user);

        return userMapper.toDto(user);
    }

    public void deleteUser(Long userId) {
        var user = userRepository.findById(userId).orElse(null);
        if (user == null)
            throw new UserNotFoundException();
        userRepository.delete(user);
    }

    // Action based update

    public void changePin(Long userId, ChangePinRequest request) {
        var user = userRepository.findById(userId).orElse(null);
        if (user == null)
            throw new UserNotFoundException();

        if (!user.getPin().equals(request.oldPin()))
            throw new PinMisMatchException();
        // hash pin here
        user.setPin(request.newPin());
        userRepository.save(user);
    }

    public void changePassword(Long userId, ChangePasswordRequest request) {
        var user = userRepository.findById(userId).orElse(null);
        if (user == null)
            throw new UserNotFoundException();

        // hash password here
        if (!user.getPassword().equals(request.oldPassword()))
            throw new PasswordMisMatchException();

        user.setPassword(request.newPassword());
        userRepository.save(user);
    }

    public UserWalletDto wallet(Long userId) {
        var wallet = userRepository.getWalletByUserId(userId).orElse(null);
        if (wallet == null)
            throw new WalletNotFoundException();
        return new UserWalletDto(wallet.getBalance());
    }

}
