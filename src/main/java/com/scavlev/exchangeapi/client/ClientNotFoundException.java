package com.scavlev.exchangeapi.client;

import static java.lang.String.format;

public class ClientNotFoundException extends RuntimeException {

    public ClientNotFoundException(Long clientId) {
        super(format("Client with id %d is not found", clientId));
    }
}
