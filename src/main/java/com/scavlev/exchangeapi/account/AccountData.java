package com.scavlev.exchangeapi.account;

import com.scavlev.exchangeapi.account.domain.Account;
import com.scavlev.exchangeapi.account.domain.AccountStatus;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Builder
public class AccountData {

    @NotNull
    private final Long id;

    @NotNull
    private final Long clientId;

    @NotNull
    private final String currency;

    @NotNull
    private final BigDecimal balance;

    @NotNull
    private final AccountStatus status;

    public static AccountData fromAccount(Account account) {
        return AccountData.builder()
                .id(account.getId())
                .clientId(account.getClient().getId())
                .balance(account.getBalance())
                .currency(account.getCurrency())
                .status(account.getStatus())
                .build();
    }
}
