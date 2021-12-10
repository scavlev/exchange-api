package com.scavlev.exchangeapi.client.services;

import com.scavlev.exchangeapi.client.ClientData;
import com.scavlev.exchangeapi.client.ClientNotFoundException;
import com.scavlev.exchangeapi.client.UpdateClientRequest;
import com.scavlev.exchangeapi.client.domain.Client;
import com.scavlev.exchangeapi.client.domain.ClientRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.function.BiFunction;

@Service
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class UpdateClient implements BiFunction<Long, UpdateClientRequest, ClientData> {

    private final ClientRepository clientRepository;

    @Override
    @Transactional
    public ClientData apply(Long clientId, UpdateClientRequest updateClientRequest) {
        return clientRepository.findById(clientId)
                .map(client -> updateClient(updateClientRequest, client))
                .map(clientRepository::saveAndFlush)
                .map(ClientData::fromClient)
                .orElseThrow(() -> new ClientNotFoundException(clientId));
    }

    private Client updateClient(UpdateClientRequest updateClientRequest, Client client) {
        client.setStatus(updateClientRequest.getStatus());
        return client;
    }
}
