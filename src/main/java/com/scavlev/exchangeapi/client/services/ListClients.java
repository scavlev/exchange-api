package com.scavlev.exchangeapi.client.services;

import com.scavlev.exchangeapi.client.ClientData;
import com.scavlev.exchangeapi.client.domain.ClientRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class ListClients implements Function<Pageable, Page<ClientData>> {

    private final ClientRepository clientRepository;

    @Override
    public Page<ClientData> apply(Pageable pageRequest) {
        return clientRepository.findAll(pageRequest).map(ClientData::fromClient);
    }
}
