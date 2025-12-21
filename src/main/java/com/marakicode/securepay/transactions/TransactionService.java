package com.marakicode.securepay.transactions;

import com.marakicode.securepay.payments.PaymentProviderService;
import com.marakicode.securepay.wallets.WalletService;
import com.marakicode.securepay.users.User;
import com.marakicode.securepay.users.UserService;
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

    public void validateDifferentUsers(User sender, User receiver) {
        if (sender.getId().equals(receiver.getId())) {
            throw new CannotSendToSameUserException();
        }
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
