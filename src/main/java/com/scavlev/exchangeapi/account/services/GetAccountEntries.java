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
import org.springframework.validation.annotation.Validated;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Service
@Validated
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class GetAccountEntries {

    private final AccountEntryRepository accountEntryRepository;
    private final AccountRepository accountRepository;

    @Valid
    @Transactional
    public Page<AccountEntryData> get(@NotNull Long accountId, @NotNull Pageable pageRequest) {
        if (!accountRepository.existsById(accountId)) {
            throw new AccountEntriesRetrievalException(accountId);
        }
        return accountEntryRepository.findByAccountId(accountId, pageRequest).map(AccountEntryData::fromAccountEntry);
    }
}
