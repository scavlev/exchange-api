package com.scavlev.exchangeapi.client.services;

import com.scavlev.exchangeapi.account.AccountData;
import com.scavlev.exchangeapi.client.ClientDoesntExistException;
import com.scavlev.exchangeapi.client.domain.ClientRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class GetClientAccounts implements Function<Long, List<AccountData>> {

    private final ClientRepository clientRepository;

    @Override
    @Transactional
    public List<AccountData> apply(Long clientId) {
        return clientRepository.findById(clientId)
                .orElseThrow(() -> new ClientDoesntExistException(clientId))
                .getAccounts()
                .stream()
                .map(AccountData::fromAccount)
                .collect(Collectors.toList());
    }

}
