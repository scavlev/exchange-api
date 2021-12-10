package com.scavlev.exchangeapi.account;

public class AccountEntriesRetrievalException extends AccountNotFoundException {

    public AccountEntriesRetrievalException(Long accountId) {
        super(accountId);
    }
}
