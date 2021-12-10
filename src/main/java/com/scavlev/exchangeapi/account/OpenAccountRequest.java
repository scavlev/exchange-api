package com.scavlev.exchangeapi.account;

import com.scavlev.exchangeapi.currency.SupportedCurrency;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class OpenAccountRequest {

    @NotNull
    private final Long clientId;

    @NotNull
    @SupportedCurrency
    private final String currency;

}
