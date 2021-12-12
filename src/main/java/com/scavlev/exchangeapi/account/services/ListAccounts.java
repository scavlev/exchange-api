package com.scavlev.exchangeapi.account.services;

import com.scavlev.exchangeapi.account.AccountData;
import com.scavlev.exchangeapi.account.domain.AccountRepository;
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
public class ListAccounts {

    private final AccountRepository accountRepository;

    @Valid
    public Page<AccountData> list(@NotNull Pageable pageRequest) {
        return accountRepository.findAll(pageRequest).map(AccountData::fromAccount);
    }
}
