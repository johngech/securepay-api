package com.marakicode.securepay.services;

import com.marakicode.securepay.dtos.SendMoneyRequest;
import com.marakicode.securepay.dtos.TransactionDto;
import com.marakicode.securepay.entities.PaymentProvider;
import com.marakicode.securepay.entities.PaymentProviders;
import com.marakicode.securepay.entities.Transaction;
import com.marakicode.securepay.entities.TransactionParticipant;
import com.marakicode.securepay.entities.TransactionStatus;
import com.marakicode.securepay.entities.TransactionType;
import com.marakicode.securepay.entities.User;
import com.marakicode.securepay.entities.Wallet;
import com.marakicode.securepay.exceptions.InsufficientBalanceException;
import com.marakicode.securepay.exceptions.InvalidPinException;
import com.marakicode.securepay.exceptions.SameAccountSendException;
import com.marakicode.securepay.exceptions.TransactionNotFoundException;
import com.marakicode.securepay.exceptions.UserNotFoundException;
import com.marakicode.securepay.exceptions.WalletNotFoundException;
import com.marakicode.securepay.mappers.TransactionMapper;
import com.marakicode.securepay.repositories.PaymentProviderRepository;
import com.marakicode.securepay.repositories.TransactionRepository;
import com.marakicode.securepay.repositories.UserRepository;
import com.marakicode.securepay.repositories.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final UserRepository userRepository;
    private final PaymentProviderRepository paymentProviderRepository;
    private final WalletRepository walletRepository;

    public List<TransactionDto> getAllTransactions() {
        var transactions = transactionRepository.findAll();
        return transactionMapper.toDtoList(transactions);
    }

    public List<TransactionDto> getSentTransactionByUserId(Long userId) {
        var transactions = transactionRepository.getSentTransactionByUserId(userId);
        return transactionMapper.toDtoList(transactions);
    }

    public List<TransactionDto> getReceivedTransactionBySenderId(Long userId) {
        var transactions = transactionRepository.getReceivedTransactionBySenderId(userId);
        return transactionMapper.toDtoList(transactions);
    }

    public List<TransactionDto> getAllTransactionsByUserId(Long userId) {
        var transactions = transactionRepository.getAllTransactionsByUserId(userId);
        return transactionMapper.toDtoList(transactions);
    }

    public TransactionDto getTransactionByUserIdAndTransactionCode(Long userId, String transactionCode) {
        var transaction = transactionRepository.getTransactionByUserIdAndTransactionCode(userId, transactionCode)
                .orElseThrow(TransactionNotFoundException::new);
        return transactionMapper.toDto(transaction);
    }

    public TransactionDto getTransactionByTransactionCode(String transactionCode) {
        var transaction = transactionRepository
                .findByTransactionCode(transactionCode)
                .orElseThrow(TransactionNotFoundException::new);
        return transactionMapper.toDto(transaction);
    }

    public void deleteTransaction(Long userId, String transactionCode) {
        var transaction = transactionRepository
                .getTransactionByUserIdAndTransactionCode(userId, transactionCode)
                .orElseThrow(TransactionNotFoundException::new);
        transactionRepository.delete(transaction);
    }

    private PaymentProvider getPaymentProvider() {
        return paymentProviderRepository.findByName(PaymentProviders.STRIPE)
                .orElseGet(() -> {
                    var newProvider = new PaymentProvider();
                    newProvider.setName(PaymentProviders.STRIPE);
                    return paymentProviderRepository.save(newProvider);
                });
    }

    @Transactional
    public TransactionDto sendMoney(Long senderId, SendMoneyRequest request) {
        // validate data
        var sender = validateUserExists(senderId);
        var receiver = validateUserExists(request.getReceiverId());

        validateNotSameUsers(sender, receiver);
        validatePin(sender, request.getPin());

        var senderWallet = getWallet(senderId);
        var receiverWallet = getWallet(receiver.getId());
        validateSufficientBalance(senderWallet, request.getAmount());

        // transfer funds
        transferFunds(senderWallet, receiverWallet, request.getAmount());

        // record transactions
        var transaction = recordTransfer(sender, receiver, request.getAmount(), request.getDescription());

        return transactionMapper.toDto(transaction);
    }

    private Transaction recordTransfer(User sender, User receiver, BigDecimal amount, String description) {
        var transaction = Transaction.builder()
                .provider(getPaymentProvider())
                .type(TransactionType.TRANSFER)
                .status(TransactionStatus.PENDING)
                .amount(amount)
                .description(description)
                .transactionCode(generateTransactionCode())
                .build();

        var participant = TransactionParticipant.builder()
                .sender(sender)
                .receiver(receiver)
                .build();
        transaction.addParticipant(participant);

        return transactionRepository.save(transaction);
    }

    private void transferFunds(Wallet senderWallet, Wallet receiverWallet, BigDecimal amount) {
        senderWallet.setBalance(senderWallet.getBalance().subtract(amount));
        receiverWallet.setBalance(receiverWallet.getBalance().add(amount));
        walletRepository.saveAll(List.of(senderWallet, receiverWallet));
    }

    private void validateSufficientBalance(Wallet senderWallet, BigDecimal amount) {
        if (senderWallet.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException();
        }
    }

    private Wallet getWallet(Long userId) {
        return walletRepository.getWalletByUser(userId)
                .orElseThrow(WalletNotFoundException::new);
    }

    private void validatePin(User sender, String pin) {
        if (!sender.getPin().equals(pin))
            throw new InvalidPinException();
    }

    private void validateNotSameUsers(User sender, User receiver) {
        if (sender.getId().equals(receiver.getId())) {
            throw new SameAccountSendException();
        }
    }

    private User validateUserExists(Long senderId) {
        return userRepository.findById(senderId)
                .orElseThrow(UserNotFoundException::new);
    }

    private String generateTransactionCode() {
        var builder = new StringBuilder();
        return builder.append("TNX-")
                .append(UUID.randomUUID().toString(), 0, 8)
                .append("-")
                .append(System.currentTimeMillis() % 10000)
                .toString();
    }
}
