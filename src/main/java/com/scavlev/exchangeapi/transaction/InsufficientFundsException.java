package com.scavlev.exchangeapi.transaction;

import static java.lang.String.format;

public class InsufficientFundsException extends RuntimeException {

    public InsufficientFundsException(Long accountId) {
        super(format("Insufficient funds on account %s to perform transaction", accountId));
    }
}
