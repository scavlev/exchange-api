package com.scavlev.exchangeapi.transaction.services;

import com.scavlev.exchangeapi.transaction.TransactionData;
import com.scavlev.exchangeapi.transaction.domain.TransactionRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Optional;

@Service
@Validated
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class FindTransaction {

    private final TransactionRepository transactionRepository;

    @Valid
    @Transactional
    public Optional<TransactionData> find(@NotNull Long transactionId) {
        return transactionRepository.findById(transactionId).map(TransactionData::fromTransaction);
    }

}
