package com.scavlev.exchangeapi.account;

import com.scavlev.exchangeapi.account.domain.AccountEntry;
import com.scavlev.exchangeapi.account.domain.AccountEntryType;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class AccountEntryData {

    @NotNull
    private final Long accountId;

    @NotNull
    private final Long transactionId;

    @NotNull
    private final LocalDateTime timestamp;

    @NotNull
    private final BigDecimal amount;

    @NotNull
    private final String currency;

    @NotNull
    private final AccountEntryType type;

    public static AccountEntryData fromAccountEntry(AccountEntry accountEntry) {
        return AccountEntryData.builder()
                .accountId(accountEntry.getAccount().getId())
                .transactionId(accountEntry.getTransaction().getId())
                .amount(accountEntry.getAmount())
                .timestamp(accountEntry.getEntityCreated())
                .currency(accountEntry.getAccount().getCurrency())
                .type(accountEntry.getType())
                .build();
    }
}
