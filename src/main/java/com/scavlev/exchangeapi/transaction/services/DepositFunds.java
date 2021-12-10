package com.scavlev.exchangeapi.transaction.services;

import com.scavlev.exchangeapi.account.DeactivatedAccountAccessException;
import com.scavlev.exchangeapi.account.domain.Account;
import com.scavlev.exchangeapi.account.domain.AccountEntry;
import com.scavlev.exchangeapi.account.domain.AccountEntryType;
import com.scavlev.exchangeapi.account.domain.AccountRepository;
import com.scavlev.exchangeapi.transaction.DepositTransactionRequest;
import com.scavlev.exchangeapi.transaction.OperationOnNonExistentAccountException;
import com.scavlev.exchangeapi.transaction.TransactionData;
import com.scavlev.exchangeapi.transaction.domain.Transaction;
import com.scavlev.exchangeapi.transaction.domain.TransactionRepository;
import com.scavlev.exchangeapi.transaction.domain.TransactionType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.function.Function;

import static com.scavlev.exchangeapi.transaction.TransactionData.fromTransaction;

@Service
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class DepositFunds implements Function<DepositTransactionRequest, TransactionData> {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    @Override
    @Transactional
    public TransactionData apply(DepositTransactionRequest depositTransactionRequest) {
        Account toAccount = findAccount(depositTransactionRequest.getToAccount());

        if (toAccount.isDeactivated()) {
            throw new DeactivatedAccountAccessException("Can't deposit funds to deactivated account.");
        }

        toAccount.alterBalanceBy(depositTransactionRequest.getAmount());

        Transaction transaction = Transaction.builder()
                .transactionType(TransactionType.DEPOSIT)
                .build();

        transaction.addEntry(AccountEntry.builder()
                .account(toAccount)
                .transaction(transaction)
                .amount(depositTransactionRequest.getAmount())
                .type(AccountEntryType.DEBIT)
                .build());

        return fromTransaction(transactionRepository.saveAndFlush(transaction));
    }

    private Account findAccount(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new OperationOnNonExistentAccountException(accountId));
    }

}
