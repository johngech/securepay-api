package com.marakicode.securepay.payments;

import com.marakicode.securepay.transactions.SendMoneyRequest;
import com.marakicode.securepay.transactions.TransactionDto;
import com.marakicode.securepay.transactions.TransactionNotFoundException;
import com.marakicode.securepay.transactions.TransactionRepository;
import com.marakicode.securepay.transactions.TransactionService;
import com.marakicode.securepay.transactions.TransactionStatus;
import com.marakicode.securepay.wallets.WalletService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@AllArgsConstructor
@Service
public class PaymentService {
    private final PaymentGateway paymentGateway;
    private final TransactionService transactionService;
    private final WalletService walletService;
    private final TransactionRepository transactionRepository;

    public PaymentSessionResponse createPaymentIntent(Long userId, BigDecimal amount) {
        var transaction = transactionService.createPendingDeposit(
                userId,
                amount
        );
        var session = paymentGateway.createPaymentSession(
                new PaymentData(
                        transaction.getId(),
                        transaction.getExternalTransactionId(),
                        amount
                )
        );

        return new PaymentSessionResponse(
                transaction.getId(),
                session.provider(),
                session.sessionType(),
                transaction.getStatus(),
                transaction.getExternalTransactionId(),
                session.data()
        );
    }

    @Transactional
    public void handleWebhookRequest(WebhookRequest request) {
        paymentGateway.parseWebhookRequest(request)
                .ifPresent(result -> {

                    var transaction = transactionRepository.findById(result.transactionId())
                            .orElseThrow(TransactionNotFoundException::new);

                    if (transaction.getStatus() != TransactionStatus.PENDING) {
                        return;
                    }

                    transaction.setStatus(result.status());
                    transactionRepository.save(transaction);

                    if (result.status() == TransactionStatus.COMPLETED) {
                        var receiver = transaction.getParticipants()
                                .stream()
                                .findFirst()
                                .orElseThrow()
                                .getReceiver();

                        walletService.credit(receiver.getId(), result.amount());
                    }

                });
    }

    @Transactional
    public TransactionDto sendMoney(Long senderId, SendMoneyRequest request) {
        return transactionService.sendMoney(senderId, request);
    }

    public BigDecimal getWalletBalance(Long userId) {
        return walletService.getWalletByUserId(userId).getBalance();
    }
}
