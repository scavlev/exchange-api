package com.scavlev.exchangeapi.client.services;

import com.scavlev.exchangeapi.account.domain.AccountRepository;
import com.scavlev.exchangeapi.client.ClientData;
import com.scavlev.exchangeapi.client.ClientNotFoundException;
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
public class DeactivateClient {

    private final ClientRepository clientRepository;
    private final AccountRepository accountRepository;

    @Valid
    @Transactional
    public ClientData deactivate(@NotNull Long clientId) {
        clientRepository.deactivateClient(clientId);
        accountRepository.deactivateClientAccounts(clientId);

        return clientRepository.findById(clientId)
                .map(ClientData::fromClient)
                .orElseThrow(() -> new ClientNotFoundException(clientId));
    }
}
