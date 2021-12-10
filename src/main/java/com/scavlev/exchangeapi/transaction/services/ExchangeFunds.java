package com.scavlev.exchangeapi.transaction.services;

import com.scavlev.exchangeapi.account.domain.Account;
import com.scavlev.exchangeapi.account.domain.AccountEntry;
import com.scavlev.exchangeapi.account.domain.AccountEntryType;
import com.scavlev.exchangeapi.account.domain.AccountRepository;
import com.scavlev.exchangeapi.transaction.OperationOnNonExistentAccountException;
import com.scavlev.exchangeapi.transaction.ProcessTransactionRequest;
import com.scavlev.exchangeapi.transaction.TransactionData;
import com.scavlev.exchangeapi.transaction.domain.ExchangeRate;
import com.scavlev.exchangeapi.transaction.domain.Transaction;
import com.scavlev.exchangeapi.transaction.domain.TransactionRepository;
import com.scavlev.exchangeapi.transaction.domain.TransactionType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.function.BiFunction;

import static com.scavlev.exchangeapi.transaction.TransactionData.fromTransaction;

@Service
@AllArgsConstructor(access = AccessLevel.PACKAGE)
class ExchangeFunds implements BiFunction<ProcessTransactionRequest, BigDecimal, TransactionData> {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    @Override
    @Transactional
    public TransactionData apply(ProcessTransactionRequest processTransactionRequest, BigDecimal rate) {
        Account fromAccount = findAccount(processTransactionRequest.getFromAccount());
        Account toAccount = findAccount(processTransactionRequest.getToAccount());

        BigDecimal exchangeAmount = processTransactionRequest.getAmount();
        BigDecimal fromAmount = exchangeAmount.negate();
        BigDecimal toAmount = exchangeAmount.multiply(rate).setScale(2, RoundingMode.HALF_DOWN);

        if (toAmount.compareTo(new BigDecimal("0.01")) < 0) {
            throw new InvalidReceivableAmount("Receivable amount is less than 0.01");
        }

        Transaction transaction = Transaction.builder()
                .transactionType(TransactionType.EXCHANGE)
                .build();

        transaction.setExchangeRate(ExchangeRate.builder()
                .fromCurrency(fromAccount.getCurrency())
                .toCurrency(toAccount.getCurrency())
                .rate(rate)
                .transaction(transaction)
                .build());

        fromAccount.alterBalanceBy(fromAmount);
        transaction.addEntry(AccountEntry.builder()
                .account(fromAccount)
                .transaction(transaction)
                .amount(fromAmount)
                .type(AccountEntryType.CREDIT)
                .build());

        toAccount.alterBalanceBy(toAmount);
        transaction.addEntry(AccountEntry.builder()
                .account(toAccount)
                .transaction(transaction)
                .amount(toAmount)
                .type(AccountEntryType.DEBIT)
                .build());

        return fromTransaction(transactionRepository.saveAndFlush(transaction));
    }

    private Account findAccount(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new OperationOnNonExistentAccountException(accountId));
    }

}
