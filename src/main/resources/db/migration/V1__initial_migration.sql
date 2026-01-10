CREATE TABLE users
(
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(50)  NOT NULL,
    last_name  VARCHAR(50)  NOT NULL,
    password   VARCHAR(255) NOT NULL,
    phone      VARCHAR(20)  NOT NULL UNIQUE,
    email      VARCHAR(100) NOT NULL UNIQUE,
    pin        VARCHAR(255) NULL
);

CREATE TABLE wallets
(
    id      INT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT UNIQUE NOT NULL,
    balance DECIMAL(10, 2) DEFAULT 0.00,
    CONSTRAINT fk_wallet_user
        FOREIGN KEY (user_id)
            REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE payment_providers
(
    id         INT PRIMARY KEY AUTO_INCREMENT,
    provider_type       ENUM ('PAYPAL', 'STRIPE', 'TELEBIRR') DEFAULT 'STRIPE',
    is_active  BOOLEAN   NOT NULL                    DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL                    DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE transactions
(
    id               BIGINT PRIMARY KEY AUTO_INCREMENT,
    transaction_code CHAR(26)       NOT NULL UNIQUE,
    provider_id      INT            NOT NULL,
    amount           DECIMAL(10, 2) NOT NULL,
    type             ENUM ('TRANSFER', 'DEPOSIT')            DEFAULT 'TRANSFER',
    status           ENUM ('PENDING', 'COMPLETED', 'FAILED') DEFAULT 'PENDING',
    description      VARCHAR(255),

    CONSTRAINT fk_transaction_provider
        FOREIGN KEY (provider_id) REFERENCES payment_providers (id),
    INDEX idx_provider (provider_id)
);

CREATE TABLE transaction_participants
(
    id             BIGINT PRIMARY KEY AUTO_INCREMENT,
    transaction_id BIGINT NOT NULL,
    sender_id      BIGINT NOT NULL,
    receiver_id    BIGINT NOT NULL,
    involved_at    DATETIME DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_tp_transaction
        FOREIGN KEY (transaction_id)
            REFERENCES transactions (id)
            ON DELETE CASCADE,

    CONSTRAINT fk_tp_sender
        FOREIGN KEY (sender_id)
            REFERENCES users (id),

    CONSTRAINT fk_tp_receiver
        FOREIGN KEY (receiver_id)
            REFERENCES users (id),

    INDEX idx_tp_transaction (transaction_id),
    INDEX idx_tp_sender (sender_id),
    INDEX idx_tp_receiver (receiver_id),

    UNIQUE (transaction_id)
);
