package com.scavlev.exchangeapi.transaction;

public class TransactionEntryLoadException extends TransactionNotFoundException {

    public TransactionEntryLoadException(Long transactionId) {
        super(transactionId);
    }
}
