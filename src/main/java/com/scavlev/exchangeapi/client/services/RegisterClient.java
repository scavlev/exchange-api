package com.scavlev.exchangeapi.client.services;

import com.scavlev.exchangeapi.client.ClientData;
import com.scavlev.exchangeapi.client.domain.Client;
import com.scavlev.exchangeapi.client.domain.ClientRepository;
import com.scavlev.exchangeapi.client.domain.ClientStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

import static com.scavlev.exchangeapi.client.ClientData.fromClient;

@Service
@Validated
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class RegisterClient {

    private final ClientRepository clientRepository;

    @Valid
    public ClientData register() {
        Client client = Client.builder()
                .status(ClientStatus.ACTIVE)
                .build();

        return fromClient(clientRepository.saveAndFlush(client));
    }

}
