package com.scavlev.exchangeapi.transaction.services;

import com.scavlev.exchangeapi.account.AccountEntryData;
import com.scavlev.exchangeapi.transaction.TransactionEntryLoadException;
import com.scavlev.exchangeapi.transaction.domain.TransactionRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class GetTransactionEntries implements Function<Long, List<AccountEntryData>> {

    private final TransactionRepository transactionRepository;

    @Override
    @Transactional
    public List<AccountEntryData> apply(Long transactionId) {
        return transactionRepository.findById(transactionId)
                .orElseThrow(() -> new TransactionEntryLoadException(transactionId))
                .getEntries()
                .stream()
                .map(AccountEntryData::fromAccountEntry)
                .collect(Collectors.toList());
    }

}
