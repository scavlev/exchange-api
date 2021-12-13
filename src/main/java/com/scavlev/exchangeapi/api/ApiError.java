package com.scavlev.exchangeapi.api;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

@Data
@Builder
class ApiError {

    @NotNull
    private final ZonedDateTime timestamp;

    @NotNull
    private final int status;

    @NotNull
    private final String error;

    @NotNull
    private final String path;

}
