package com.scavlev.exchangeapi.account.services;

import com.scavlev.exchangeapi.account.AccountData;
import com.scavlev.exchangeapi.account.domain.AccountRepository;
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
public class FindAccount {

    private final AccountRepository accountRepository;

    @Valid
    public Optional<AccountData> find(@NotNull Long accountId) {
        return accountRepository.findById(accountId).map(AccountData::fromAccount);
    }
}
