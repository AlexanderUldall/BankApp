CREATE TABLE customers (
    customer_id INTEGER NOT NULL AUTO_INCREMENT,
    social_security_number VARCHAR(11) UNIQUE,
    creation_date_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    name VARCHAR(100) NOT NULL,
    PRIMARY KEY (customer_id)
);

CREATE TABLE bank_accounts (
    bank_Account_id INTEGER NOT NULL AUTO_INCREMENT,
    CURRENCY_CODE   VARCHAR(3)  NOT NULL,
    ACCOUNT_TYPE   VARCHAR(3)  NOT NULL,
    creation_date_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    balance DECIMAL(10,2) DEFAULT 0.00,
    PRIMARY KEY (bank_Account_id),
    customer_id INTEGER NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
);

CREATE TABLE transactions (
    transaction_id INTEGER NOT NULL AUTO_INCREMENT,
    CURRENCY_CODE   VARCHAR(3)  NOT NULL,
    amount  DECIMAL(10,2)  NOT NULL,
    creation_date_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    bank_Account_id INTEGER NOT NULL,
    PRIMARY KEY (transaction_id),
    FOREIGN KEY (bank_Account_id) REFERENCES bank_accounts(bank_Account_id)
);
