package com.scavlev.exchangeapi.client.services;

import com.scavlev.exchangeapi.client.ClientData;
import com.scavlev.exchangeapi.client.domain.ClientRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Optional;

@Service
@Validated
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class FindClient {

    private final ClientRepository clientRepository;

    @Valid
    public Optional<ClientData> find(@NotNull Long clientId) {
        return clientRepository.findById(clientId).map(ClientData::fromClient);
    }
}
