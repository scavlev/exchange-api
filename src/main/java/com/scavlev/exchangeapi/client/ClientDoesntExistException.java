package com.scavlev.exchangeapi.client;

public class ClientDoesntExistException extends ClientNotFoundException {

    public ClientDoesntExistException(Long clientId) {
        super(clientId);
    }
}
