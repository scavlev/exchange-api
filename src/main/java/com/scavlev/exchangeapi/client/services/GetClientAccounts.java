package com.scavlev.exchangeapi.client.services;

import com.scavlev.exchangeapi.account.AccountData;
import com.scavlev.exchangeapi.client.ClientDoesntExistException;
import com.scavlev.exchangeapi.client.domain.ClientRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Validated
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class GetClientAccounts {

    private final ClientRepository clientRepository;

    @Valid
    @Transactional
    public List<AccountData> get(@NotNull Long clientId) {
        return clientRepository.findById(clientId)
                .orElseThrow(() -> new ClientDoesntExistException(clientId))
                .getAccounts()
                .stream()
                .map(AccountData::fromAccount)
                .collect(Collectors.toList());
    }

}
