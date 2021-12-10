package com.scavlev.exchangeapi.account.services;

import com.scavlev.exchangeapi.account.AccountData;
import com.scavlev.exchangeapi.account.domain.AccountRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class ListAccounts implements Function<Pageable, Page<AccountData>> {

    private final AccountRepository accountRepository;

    @Override
    public Page<AccountData> apply(Pageable pageRequest) {
        return accountRepository.findAll(pageRequest).map(AccountData::fromAccount);
    }
}
