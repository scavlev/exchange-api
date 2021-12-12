package com.scavlev.exchangeapi.transaction.services;

import com.scavlev.exchangeapi.transaction.TransactionData;
import com.scavlev.exchangeapi.transaction.domain.TransactionRepository;
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
public class ListTransactions {

    private final TransactionRepository transactionRepository;

    @Valid
    public Page<TransactionData> list(@NotNull Pageable pageRequest) {
        return transactionRepository.findAll(pageRequest).map(TransactionData::fromTransaction);
    }

}
