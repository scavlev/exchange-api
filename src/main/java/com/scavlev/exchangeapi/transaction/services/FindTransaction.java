package com.scavlev.exchangeapi.transaction.services;

import com.scavlev.exchangeapi.transaction.TransactionData;
import com.scavlev.exchangeapi.transaction.domain.TransactionRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.function.Function;

@Service
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class FindTransaction implements Function<Long, Optional<TransactionData>> {

    private final TransactionRepository transactionRepository;

    @Override
    @Transactional
    public Optional<TransactionData> apply(Long transactionId) {
        return transactionRepository.findById(transactionId).map(TransactionData::fromTransaction);
    }

}
