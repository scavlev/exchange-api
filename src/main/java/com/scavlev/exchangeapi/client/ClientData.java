package com.scavlev.exchangeapi.client;

import com.scavlev.exchangeapi.client.domain.Client;
import com.scavlev.exchangeapi.client.domain.ClientStatus;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder
public class ClientData {

    @NotNull
    private final Long id;

    @NotNull
    private final ClientStatus status;

    public static ClientData fromClient(Client client) {
        return ClientData.builder()
                .id(client.getId())
                .status(client.getStatus())
                .build();
    }
}
