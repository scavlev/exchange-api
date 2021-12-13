package com.scavlev.exchangeapi.transaction;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.scavlev.exchangeapi.transaction.domain.Transaction;
import com.scavlev.exchangeapi.transaction.domain.TransactionType;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_ABSENT;
import static java.util.Optional.ofNullable;

@Data
@Builder
public class TransactionData {

    @NotNull
    private final Long id;

    @NotNull
    private final OffsetDateTime timestamp;

    @NotNull
    private final TransactionType type;

    private final Rate exchangeRate;

    private final FromAccount fromAccount;

    private final ToAccount toAccount;

    public static TransactionData fromTransaction(Transaction transaction) {
        TransactionData.TransactionDataBuilder transactionResponseBuilder = TransactionData.builder()
                .id(transaction.getId())
                .type(transaction.getTransactionType())
                .timestamp(transaction.getEntityCreated());

        transaction.getExchangeRate().ifPresent(exchangeRate ->
                transactionResponseBuilder.exchangeRate(TransactionData.Rate.builder()
                        .rate(exchangeRate.getRate())
                        .fromCurrency(exchangeRate.getFromCurrency())
                        .toCurrency(exchangeRate.getToCurrency())
                        .build()));

        transaction.getDebitAccountEntry()
                .ifPresent(debitAccountEntry -> transactionResponseBuilder.toAccount(TransactionData.ToAccount.builder()
                        .id(debitAccountEntry.getAccount().getId())
                        .currency(debitAccountEntry.getAccount().getCurrency())
                        .amount(debitAccountEntry.getAmount())
                        .build()));

        transaction.getCreditAccountEntry()
                .ifPresent(creditAccountEntry -> transactionResponseBuilder.fromAccount(TransactionData.FromAccount.builder()
                        .id(creditAccountEntry.getAccount().getId())
                        .currency(creditAccountEntry.getAccount().getCurrency())
                        .amount(creditAccountEntry.getAmount())
                        .build()));

        return transactionResponseBuilder.build();
    }

    @JsonInclude(NON_ABSENT)
    public Optional<Rate> getExchangeRate() {
        return ofNullable(exchangeRate);
    }

    @JsonInclude(NON_ABSENT)
    public Optional<FromAccount> getFromAccount() {
        return ofNullable(fromAccount);
    }

    @JsonInclude(NON_ABSENT)
    public Optional<ToAccount> getToAccount() {
        return ofNullable(toAccount);
    }

    @Data
    @Builder
    public static class Rate {
        @NotNull
        private final BigDecimal rate;
        @NotNull
        private final String fromCurrency;
        @NotNull
        private final String toCurrency;
    }

    @Data
    @Builder
    public static class FromAccount {
        @NotNull
        private final Long id;
        @NotNull
        private final String currency;
        @NotNull
        private final BigDecimal amount;
    }

    @Data
    @Builder
    public static class ToAccount {
        @NotNull
        private final Long id;
        @NotNull
        private final String currency;
        @NotNull
        private final BigDecimal amount;
    }
}
