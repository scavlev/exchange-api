package com.scavlev.exchangeapi.account.services;

import com.scavlev.exchangeapi.account.AccountData;
import com.scavlev.exchangeapi.account.OpenAccountRequest;
import com.scavlev.exchangeapi.account.domain.Account;
import com.scavlev.exchangeapi.account.domain.AccountRepository;
import com.scavlev.exchangeapi.account.domain.AccountStatus;
import com.scavlev.exchangeapi.client.ClientDoesntExistException;
import com.scavlev.exchangeapi.client.domain.Client;
import com.scavlev.exchangeapi.client.domain.ClientRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.math.BigDecimal;

import static com.scavlev.exchangeapi.account.AccountData.fromAccount;

@Service
@Validated
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class OpenAccount {

    private final AccountRepository accountRepository;
    private final ClientRepository clientRepository;

    @Valid
    public AccountData open(@Valid OpenAccountRequest openAccountRequest) {
        Client client = clientRepository.findById(openAccountRequest.getClientId())
                .orElseThrow(() -> new ClientDoesntExistException(openAccountRequest.getClientId()));

        Account account = Account.builder()
                .client(client)
                .currency(openAccountRequest.getCurrency().toUpperCase())
                .balance(BigDecimal.ZERO)
                .status(AccountStatus.ACTIVE)
                .build();

        return fromAccount(accountRepository.saveAndFlush(account));
    }
}
