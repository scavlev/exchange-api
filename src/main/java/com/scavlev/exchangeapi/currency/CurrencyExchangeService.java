package com.scavlev.exchangeapi.currency;

import feign.FeignException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class CurrencyExchangeService {

    private final FreecurrencyapiApi freecurrencyapiApi;

    public BigDecimal getRate(String baseCurrency, String targetCurrency) {
        try {
            FreecurrencyapiLatestData freecurrencyapiLatestData = freecurrencyapiApi.getLatestRates(baseCurrency);
            return freecurrencyapiLatestData.getData().computeIfAbsent(targetCurrency, value -> {
                throw new CurrencyPairNotFoundException(baseCurrency, targetCurrency);
            });
        } catch (FeignException exception) {
            throw new CurrencyExchangeRatesUnavailableException(baseCurrency, targetCurrency, exception);
        }
    }

}
