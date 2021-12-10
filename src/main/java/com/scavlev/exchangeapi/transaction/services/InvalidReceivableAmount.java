package com.scavlev.exchangeapi.transaction.services;

public class InvalidReceivableAmount extends RuntimeException {

    public InvalidReceivableAmount(String message) {
        super(message);
    }

}
