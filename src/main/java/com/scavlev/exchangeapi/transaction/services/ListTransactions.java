package com.scavlev.exchangeapi.transaction.services;

import com.scavlev.exchangeapi.transaction.TransactionData;
import com.scavlev.exchangeapi.transaction.domain.TransactionRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class ListTransactions implements Function<Pageable, Page<TransactionData>> {

    private final TransactionRepository transactionRepository;

    @Override
    public Page<TransactionData> apply(Pageable pageRequest) {
        return transactionRepository.findAll(pageRequest).map(TransactionData::fromTransaction);
    }

}
