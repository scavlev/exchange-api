package com.scavlev.exchangeapi.client.services;

import com.scavlev.exchangeapi.client.ClientData;
import com.scavlev.exchangeapi.client.domain.Client;
import com.scavlev.exchangeapi.client.domain.ClientRepository;
import com.scavlev.exchangeapi.client.domain.ClientStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.Callable;

import static com.scavlev.exchangeapi.client.ClientData.fromClient;

@Service
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class RegisterClient implements Callable<ClientData> {

    private final ClientRepository clientRepository;

    @Override
    public ClientData call() {
        Client client = Client.builder()
                .status(ClientStatus.ACTIVE)
                .build();

        return fromClient(clientRepository.saveAndFlush(client));
    }

}
