ALTER TABLE transaction_participants
    ADD CONSTRAINT chk_sender_receiver_different
        CHECK (sender_id <> receiver_id);

ALTER TABLE transactions
    ADD CONSTRAINT chk_transaction_amount_positive
        CHECK (amount > 0);
