package com.scavlev.exchangeapi.account.services;

import com.scavlev.exchangeapi.account.AccountEntriesRetrievalException;
import com.scavlev.exchangeapi.account.AccountEntryData;
import com.scavlev.exchangeapi.account.domain.AccountEntryRepository;
import com.scavlev.exchangeapi.account.domain.AccountRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.function.BiFunction;

@Service
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class GetAccountEntries implements BiFunction<Long, Pageable, Page<AccountEntryData>> {

    private final AccountEntryRepository accountEntryRepository;
    private final AccountRepository accountRepository;

    @Override
    @Transactional
    public Page<AccountEntryData> apply(Long accountId, Pageable pageRequest) {
        if (!accountRepository.existsById(accountId)) {
            throw new AccountEntriesRetrievalException(accountId);
        }
        return accountEntryRepository.findByAccountId(accountId, pageRequest).map(AccountEntryData::fromAccountEntry);
    }
}
