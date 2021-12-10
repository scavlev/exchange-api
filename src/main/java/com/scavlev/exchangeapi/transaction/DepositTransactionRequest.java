package com.scavlev.exchangeapi.transaction;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
public class DepositTransactionRequest {

    @NotNull
    private final Long toAccount;

    @NotNull
    @Positive
    @DecimalMin("0.01")
    private final BigDecimal amount;

}
