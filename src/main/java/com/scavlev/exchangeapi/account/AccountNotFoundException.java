package com.scavlev.exchangeapi.account;

import static java.lang.String.format;

public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException(Long accountId) {
        super(format("Account with id %d is not found", accountId));
    }
}
