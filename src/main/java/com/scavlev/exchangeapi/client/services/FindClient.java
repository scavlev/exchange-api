package com.scavlev.exchangeapi.client.services;

import com.scavlev.exchangeapi.client.ClientData;
import com.scavlev.exchangeapi.client.domain.ClientRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Function;

@Service
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class FindClient implements Function<Long, Optional<ClientData>> {

    private final ClientRepository clientRepository;

    @Override
    public Optional<ClientData> apply(Long clientId) {
        return clientRepository.findById(clientId).map(ClientData::fromClient);
    }
}
