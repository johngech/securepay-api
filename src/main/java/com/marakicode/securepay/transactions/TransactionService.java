package com.marakicode.securepay.transactions;

import com.marakicode.securepay.payments.PaymentProviderService;
import com.marakicode.securepay.wallets.WalletService;
import com.marakicode.securepay.users.User;
import com.marakicode.securepay.users.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final PaymentProviderService paymentProviderService;
    private final WalletService walletService;
    private final UserService userService;

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

    public TransactionDto getTransactionByUserIdAndTransactionCode(
            Long userId, String transactionCode) {
        var transaction = transactionRepository
                .getTransactionByUserIdAndTransactionCode(userId, transactionCode)
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

    @Transactional
    public TransactionDto sendMoney(Long senderId, SendMoneyRequest request) {

        var sender = userService.getUserEntity(senderId);
        var receiver = userService.getUserEntity(request.getReceiverId());

        validateDifferentUsers(sender, receiver);
        walletService.validatePin(sender, request.getPin());

        var senderWallet = walletService.getWalletByUserId(senderId);
        var receiverWallet = walletService.getWalletByUserId(receiver.getId());
        walletService.validateSufficientBalance(senderWallet, request.getAmount());

        walletService.transfer(senderWallet, receiverWallet, request.getAmount());

        var transaction = recordTransfer(sender, receiver, request.getAmount(), request.getDescription());

        return transactionMapper.toDto(transaction);
    }

    private Transaction recordTransfer(User sender, User receiver, BigDecimal amount, String description) {
        var provider = paymentProviderService.getPaymentProvider();
        var transaction = Transaction.builder()
                .provider(provider)
                .type(TransactionType.TRANSFER)
                .amount(amount)
                .externalTransactionId(null)
                .description(description)
                .transactionCode(generateTransactionCode())
                .build();

        var participant = TransactionParticipant.builder()
                .sender(sender)
                .receiver(receiver)
                .build();
        transaction.addParticipant(participant);
        transaction.markAsCompleted();
        return transactionRepository.save(transaction);
    }

    public Transaction createPendingDeposit(Long userId, BigDecimal amount) {
        var user = userService.getUserEntity(userId);
        var transaction = Transaction.builder()
                .provider(paymentProviderService.getPaymentProvider())
                .type(TransactionType.DEPOSIT)
                .status(TransactionStatus.PENDING) // Wallet update comes from webhook
                .amount(amount)
                .description("Wallet deposit via stripe")
                .externalTransactionId(generateExternalRef())
                .transactionCode(generateTransactionCode())
                .build();
        transaction.addParticipant(
                TransactionParticipant.builder()
                        .receiver(user)
                        .build()
        );
        transactionRepository.save(transaction);
        return transaction;
    }

    public void validateDifferentUsers(User sender, User receiver) {
        if (sender.getId().equals(receiver.getId())) {
            throw new CannotSendToSameUserException();
        }
    }

    private String generateTransactionCode() {
        return "TNX-" + UUID.randomUUID().toString().substring(0, 8) +
                "-" + (System.currentTimeMillis() % 10000);
    }

    private String generateExternalRef() {
        return "exr_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + "_" + UUID.randomUUID().toString().substring(0, 8);
    }
}
