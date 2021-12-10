package com.scavlev.exchangeapi.currency;

import feign.Param;
import feign.RequestLine;

interface FreecurrencyapiApi {

    @RequestLine("GET /latest?base_currency={baseCurrency}")
    FreecurrencyapiLatestData getLatestRates(@Param String baseCurrency);

}
