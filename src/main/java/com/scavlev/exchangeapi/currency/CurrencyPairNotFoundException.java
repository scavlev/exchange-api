package com.scavlev.exchangeapi.currency;

import static java.lang.String.format;

public class CurrencyPairNotFoundException extends RuntimeException {

    public CurrencyPairNotFoundException(String baseCurrency, String targetCurrency) {
        super(format("Currency pair %s not found for currency %s", targetCurrency, baseCurrency));
    }

}
