package com.scavlev.exchangeapi.client;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.scavlev.exchangeapi.client.domain.ClientStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor(onConstructor = @__(@JsonCreator(mode = JsonCreator.Mode.PROPERTIES)))
public class UpdateClientRequest {

    @NotNull
    private final ClientStatus status;

}
