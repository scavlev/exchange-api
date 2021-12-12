package com.scavlev.exchangeapi.transaction;

public class InvalidReceivableAmount extends RuntimeException {

    public InvalidReceivableAmount(String message) {
        super(message);
    }

}
