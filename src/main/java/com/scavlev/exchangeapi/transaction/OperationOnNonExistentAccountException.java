package com.scavlev.exchangeapi.transaction;

import com.scavlev.exchangeapi.account.AccountNotFoundException;

public class OperationOnNonExistentAccountException extends AccountNotFoundException {

    public OperationOnNonExistentAccountException(Long accountId) {
        super(accountId);
    }

}
