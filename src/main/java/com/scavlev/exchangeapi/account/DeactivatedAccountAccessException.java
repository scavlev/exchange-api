package com.scavlev.exchangeapi.account;

public class DeactivatedAccountAccessException extends RuntimeException {

    public DeactivatedAccountAccessException(String message) {
        super(message);
    }
}
