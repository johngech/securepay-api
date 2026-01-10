ALTER TABLE transactions
    ADD COLUMN external_transaction_id VARCHAR(255);
ALTER TABLE transactions
    ADD CONSTRAINT uq_transactions_external_ref
        UNIQUE (provider_id, external_transaction_id);
