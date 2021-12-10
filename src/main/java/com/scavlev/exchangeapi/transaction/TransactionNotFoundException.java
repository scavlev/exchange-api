package com.scavlev.exchangeapi.transaction;

import static java.lang.String.format;

public class TransactionNotFoundException extends RuntimeException {

    public TransactionNotFoundException(Long accountId) {
        super(format("Transaction with id %d is not found", accountId));
    }
}
