package com.scavlev.exchangeapi.client.services;

import com.scavlev.exchangeapi.client.ClientData;
import com.scavlev.exchangeapi.client.domain.ClientRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Service
@Validated
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class ListClients {

    private final ClientRepository clientRepository;

    @Valid
    public Page<ClientData> list(@NotNull Pageable pageRequest) {
        return clientRepository.findAll(pageRequest).map(ClientData::fromClient);
    }
}
