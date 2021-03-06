package com.scavlev.exchangeapi.client.services;

import com.scavlev.exchangeapi.client.ClientData;
import com.scavlev.exchangeapi.client.ClientNotFoundException;
import com.scavlev.exchangeapi.client.UpdateClientRequest;
import com.scavlev.exchangeapi.client.domain.Client;
import com.scavlev.exchangeapi.client.domain.ClientRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Service
@Validated
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class UpdateClient {

    private final ClientRepository clientRepository;

    @Valid
    @Transactional
    public ClientData update(@NotNull Long clientId, @Valid UpdateClientRequest updateClientRequest) {
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
