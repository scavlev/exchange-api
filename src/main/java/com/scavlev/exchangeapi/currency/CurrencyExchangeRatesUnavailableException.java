package com.scavlev.exchangeapi.currency;

import static java.lang.String.format;

public class CurrencyExchangeRatesUnavailableException extends RuntimeException {

    public CurrencyExchangeRatesUnavailableException(String baseCurrency, String targetCurrency, Throwable cause) {
        super(format("Unable to retrieve currency exchange rates for %s/%s pair.", baseCurrency, targetCurrency), cause);
    }

}
