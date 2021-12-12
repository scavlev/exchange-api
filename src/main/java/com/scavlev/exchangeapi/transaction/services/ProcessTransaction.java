package com.scavlev.exchangeapi.transaction.services;

import com.scavlev.exchangeapi.account.DeactivatedAccountAccessException;
import com.scavlev.exchangeapi.account.domain.Account;
import com.scavlev.exchangeapi.account.domain.AccountRepository;
import com.scavlev.exchangeapi.currency.CurrencyExchangeService;
import com.scavlev.exchangeapi.transaction.InsufficientFundsException;
import com.scavlev.exchangeapi.transaction.OperationOnNonExistentAccountException;
import com.scavlev.exchangeapi.transaction.ProcessTransactionRequest;
import com.scavlev.exchangeapi.transaction.TransactionData;
import com.scavlev.exchangeapi.transaction.domain.TransactionType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.math.BigDecimal;

@Service
@Validated
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class ProcessTransaction {

    private final AccountRepository accountRepository;
    private final CurrencyExchangeService currencyExchangeService;
    private final ExchangeFunds exchangeFunds;
    private final TransferFunds transferFunds;

    @Valid
    public TransactionData process(@Valid ProcessTransactionRequest processTransactionRequest) {
        Account fromAccount = findAccount(processTransactionRequest.getFromAccount());
        Account toAccount = findAccount(processTransactionRequest.getToAccount());

        validateFromAccount(fromAccount, processTransactionRequest.getAmount());
        validateToAccount(toAccount);

        TransactionType transactionType = fromAccount.getCurrency().equals(toAccount.getCurrency())
                ? TransactionType.TRANSFER
                : TransactionType.EXCHANGE;

        if (transactionType == TransactionType.TRANSFER) {
            return transferFunds.transfer(processTransactionRequest);
        } else {
            BigDecimal rate = currencyExchangeService.getRate(fromAccount.getCurrency(), toAccount.getCurrency());
            return exchangeFunds.exchange(processTransactionRequest, rate);
        }
    }

    private void validateToAccount(Account toAccount) {
        if (toAccount.isDeactivated()) {
            throw new DeactivatedAccountAccessException("Can't transfer funds to deactivated account.");
        }
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
