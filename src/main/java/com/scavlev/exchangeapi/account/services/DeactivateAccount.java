package com.scavlev.exchangeapi.account.services;

import com.scavlev.exchangeapi.account.AccountData;
import com.scavlev.exchangeapi.account.AccountNotFoundException;
import com.scavlev.exchangeapi.account.domain.AccountRepository;
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
public class DeactivateAccount {

    private final AccountRepository accountRepository;

    @Valid
    @Transactional
    public AccountData deactivate(@NotNull Long accountId) {
        accountRepository.deactivateAccount(accountId);

        return accountRepository.findById(accountId)
                .map(AccountData::fromAccount)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
    }
}
