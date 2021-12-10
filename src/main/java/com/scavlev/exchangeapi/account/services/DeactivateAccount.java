package com.scavlev.exchangeapi.account.services;

import com.scavlev.exchangeapi.account.AccountData;
import com.scavlev.exchangeapi.account.AccountNotFoundException;
import com.scavlev.exchangeapi.account.domain.AccountRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.function.Function;

@Service
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class DeactivateAccount implements Function<Long, AccountData> {

    private final AccountRepository accountRepository;

    @Override
    @Transactional
    public AccountData apply(Long accountId) {
        accountRepository.deactivateAccount(accountId);

        return accountRepository.findById(accountId)
                .map(AccountData::fromAccount)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
    }
}
