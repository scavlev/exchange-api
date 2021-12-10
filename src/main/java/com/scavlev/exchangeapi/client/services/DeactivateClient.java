package com.scavlev.exchangeapi.client.services;

import com.scavlev.exchangeapi.account.domain.AccountRepository;
import com.scavlev.exchangeapi.client.ClientData;
import com.scavlev.exchangeapi.client.ClientNotFoundException;
import com.scavlev.exchangeapi.client.domain.ClientRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.function.Function;

@Service
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class DeactivateClient implements Function<Long, ClientData> {

    private final ClientRepository clientRepository;
    private final AccountRepository accountRepository;

    @Override
    @Transactional
    public ClientData apply(Long clientId) {
        clientRepository.deactivateClient(clientId);
        accountRepository.deactivateClientAccounts(clientId);

        return clientRepository.findById(clientId)
                .map(ClientData::fromClient)
                .orElseThrow(() -> new ClientNotFoundException(clientId));
    }
}
