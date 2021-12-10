package com.scavlev.exchangeapi.currency;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@Jacksonized
public class FreecurrencyapiLatestData {

    @NotNull
    private final Map<String, BigDecimal> data;

}
