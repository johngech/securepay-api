package com.marakicode.securepay.users;

import com.marakicode.securepay.wallets.UserWalletDto;
import com.marakicode.securepay.wallets.Wallet;
import com.marakicode.securepay.wallets.WalletRepository;
import com.marakicode.securepay.wallets.WalletService;
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
    private final WalletRepository walletRepository;

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

    public User getUserEntity(String phone) {
        return userRepository.findByPhone(phone)
                .orElseThrow(UserNotFoundException::new);
    }

    public UserDto getUserById(Long userId) {
        return userMapper.toDto(getUserEntity(userId));
    }

    public User resolveReceiver(ReceiverIdentifier identifier) {
        if (identifier.phone() != null) {
            return userRepository.findByPhone(identifier.phone())
                    .orElseThrow(UserNotFoundException::new);
        }
        if (identifier.email() != null) {
            return userRepository.findByEmail(identifier.email())
                    .orElseThrow(UserNotFoundException::new);
        }
        throw new IllegalArgumentException("Receiver Identifier required.");
    }

    public UserDto getUserByPhone(String phone) {
        return userMapper.toDto(getUserEntity(phone));
    }

    public UserWalletDto getUserWallet(Long userId) {
        return walletService.getWalletDto(userId);
    }

    public UserDto registerUser(UserRegisterRequest request) {
        validateRegistration(request);

        var user = userMapper.toEntity(request);
        user.setPassword(encoder.encode(request.password()));

        userRepository.save(user);

        // Create a wallet for user
        var wallet = new Wallet();
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setUser(user);
        walletRepository.save(wallet);

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

        if (user.getPin() != null && !encoder.matches(request.oldPin(), user.getPin())) {
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
