package com.scavlev.exchangeapi.transaction.services;

import com.scavlev.exchangeapi.account.AccountEntryData;
import com.scavlev.exchangeapi.transaction.TransactionEntryLoadException;
import com.scavlev.exchangeapi.transaction.domain.TransactionRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Validated
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class GetTransactionEntries {

    private final TransactionRepository transactionRepository;

    @Valid
    @Transactional
    public List<AccountEntryData> get(@NotNull Long transactionId) {
        return transactionRepository.findById(transactionId)
                .orElseThrow(() -> new TransactionEntryLoadException(transactionId))
                .getEntries()
                .stream()
                .map(AccountEntryData::fromAccountEntry)
                .collect(Collectors.toList());
    }

}
