package com.scavlev.exchangeapi.account.services;

import com.scavlev.exchangeapi.account.AccountData;
import com.scavlev.exchangeapi.account.AccountNotFoundException;
import com.scavlev.exchangeapi.account.UpdateAccountRequest;
import com.scavlev.exchangeapi.account.domain.Account;
import com.scavlev.exchangeapi.account.domain.AccountRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.function.BiFunction;

@Service
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class UpdateAccount implements BiFunction<Long, UpdateAccountRequest, AccountData> {

    private final AccountRepository accountRepository;

    @Override
    @Transactional
    public AccountData apply(Long accountId, UpdateAccountRequest updateAccountRequest) {
        return accountRepository.findById(accountId)
                .map(account -> updateAccount(updateAccountRequest, account))
                .map(accountRepository::saveAndFlush)
                .map(AccountData::fromAccount)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
    }

    private Account updateAccount(UpdateAccountRequest updateAccountRequest, Account account) {
        account.setStatus(updateAccountRequest.getStatus());
        return account;
    }
}
