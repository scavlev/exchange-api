package com.scavlev.exchangeapi.transaction.services;

import com.scavlev.exchangeapi.account.DeactivatedAccountAccessException;
import com.scavlev.exchangeapi.account.domain.Account;
import com.scavlev.exchangeapi.account.domain.AccountEntry;
import com.scavlev.exchangeapi.account.domain.AccountEntryType;
import com.scavlev.exchangeapi.account.domain.AccountRepository;
import com.scavlev.exchangeapi.transaction.InsufficientFundsException;
import com.scavlev.exchangeapi.transaction.OperationOnNonExistentAccountException;
import com.scavlev.exchangeapi.transaction.TransactionData;
import com.scavlev.exchangeapi.transaction.WithdrawalTransactionRequest;
import com.scavlev.exchangeapi.transaction.domain.Transaction;
import com.scavlev.exchangeapi.transaction.domain.TransactionRepository;
import com.scavlev.exchangeapi.transaction.domain.TransactionType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.math.BigDecimal;

import static com.scavlev.exchangeapi.transaction.TransactionData.fromTransaction;

@Service
@Validated
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class WithdrawFunds {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    @Valid
    @Transactional
    public TransactionData withdraw(@Valid WithdrawalTransactionRequest withdrawalTransactionRequest) {
        Account fromAccount = findAccount(withdrawalTransactionRequest.getFromAccount());

        validateFromAccount(fromAccount, withdrawalTransactionRequest.getAmount());

        fromAccount.alterBalanceBy(withdrawalTransactionRequest.getAmount().negate());

        Transaction transaction = Transaction.builder()
                .transactionType(TransactionType.WITHDRAWAL)
                .build();

        transaction.addEntry(AccountEntry.builder()
                .account(fromAccount)
                .transaction(transaction)
                .amount(withdrawalTransactionRequest.getAmount().negate())
                .type(AccountEntryType.CREDIT)
                .build());

        return fromTransaction(transactionRepository.saveAndFlush(transaction));
    }

    private void validateFromAccount(Account fromAccount, BigDecimal deductionAmount) {
        if (fromAccount.isDeactivated()) {
            throw new DeactivatedAccountAccessException("Can't transfer funds from deactivated account.");
        }
        if (fromAccount.getBalance().subtract(deductionAmount).compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientFundsException(fromAccount.getId());
        }
    }

    private Account findAccount(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new OperationOnNonExistentAccountException(accountId));
    }

}
