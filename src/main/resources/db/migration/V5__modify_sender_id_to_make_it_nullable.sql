-- 1. Drop the FK constraint
ALTER TABLE securepay.transaction_participants
    DROP FOREIGN KEY fk_tp_sender;

-- 2. Modify the column
ALTER TABLE securepay.transaction_participants
    MODIFY COLUMN sender_id BIGINT NULL;

-- 3. Re-add the FK
ALTER TABLE securepay.transaction_participants
    ADD CONSTRAINT fk_tp_sender
        FOREIGN KEY (sender_id) REFERENCES users(id);
