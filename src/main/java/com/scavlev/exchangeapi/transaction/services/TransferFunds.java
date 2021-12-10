package com.scavlev.exchangeapi.transaction.services;

import com.scavlev.exchangeapi.account.domain.Account;
import com.scavlev.exchangeapi.account.domain.AccountEntry;
import com.scavlev.exchangeapi.account.domain.AccountEntryType;
import com.scavlev.exchangeapi.account.domain.AccountRepository;
import com.scavlev.exchangeapi.transaction.OperationOnNonExistentAccountException;
import com.scavlev.exchangeapi.transaction.ProcessTransactionRequest;
import com.scavlev.exchangeapi.transaction.TransactionData;
import com.scavlev.exchangeapi.transaction.domain.Transaction;
import com.scavlev.exchangeapi.transaction.domain.TransactionRepository;
import com.scavlev.exchangeapi.transaction.domain.TransactionType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.function.Function;

import static com.scavlev.exchangeapi.transaction.TransactionData.fromTransaction;

@Service
@AllArgsConstructor(access = AccessLevel.PACKAGE)
class TransferFunds implements Function<ProcessTransactionRequest, TransactionData> {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    @Override
    @Transactional
    public TransactionData apply(ProcessTransactionRequest processTransactionRequest) {
        Account fromAccount = findAccount(processTransactionRequest.getFromAccount());
        Account toAccount = findAccount(processTransactionRequest.getToAccount());

        BigDecimal transferAmount = processTransactionRequest.getAmount();

        Transaction transaction = Transaction.builder()
                .transactionType(TransactionType.TRANSFER)
                .build();

        fromAccount.alterBalanceBy(transferAmount.negate());
        transaction.addEntry(AccountEntry.builder()
                .account(fromAccount)
                .transaction(transaction)
                .amount(transferAmount.negate())
                .type(AccountEntryType.CREDIT)
                .build());

        toAccount.alterBalanceBy(transferAmount);
        transaction.addEntry(AccountEntry.builder()
                .account(toAccount)
                .transaction(transaction)
                .amount(transferAmount)
                .type(AccountEntryType.DEBIT)
                .build());

        return fromTransaction(transactionRepository.saveAndFlush(transaction));
    }

    private Account findAccount(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new OperationOnNonExistentAccountException(accountId));
    }

}
