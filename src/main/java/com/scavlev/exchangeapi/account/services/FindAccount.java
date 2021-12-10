package com.scavlev.exchangeapi.account.services;

import com.scavlev.exchangeapi.account.AccountData;
import com.scavlev.exchangeapi.account.domain.AccountRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Function;

@Service
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class FindAccount implements Function<Long, Optional<AccountData>> {

    private final AccountRepository accountRepository;

    @Override
    public Optional<AccountData> apply(Long accountId) {
        return accountRepository.findById(accountId).map(AccountData::fromAccount);
    }
}
