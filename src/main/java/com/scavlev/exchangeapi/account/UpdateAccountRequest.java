package com.scavlev.exchangeapi.account;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.scavlev.exchangeapi.account.domain.AccountStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor(onConstructor = @__(@JsonCreator(mode = JsonCreator.Mode.PROPERTIES)))
public class UpdateAccountRequest {

    @NotNull
    private final AccountStatus status;

}
