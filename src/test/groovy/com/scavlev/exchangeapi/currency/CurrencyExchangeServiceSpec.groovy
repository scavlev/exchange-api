package com.scavlev.exchangeapi.currency

import feign.FeignException
import spock.lang.Specification

class CurrencyExchangeServiceSpec extends Specification {

    FreecurrencyapiApi api = Mock()
    CurrencyExchangeService currencyExchangeService = new CurrencyExchangeService(api)

    def "should get exchange rate for currency pair"() {
        given:
        def baseCurrency = "USD"
        def targetCurrency = "EUR"
        def expectedRate = 0.35
        api.getLatestRates(baseCurrency) >> new FreecurrencyapiLatestData(["EUR": expectedRate])

        when:
        def rate = currencyExchangeService.getRate(baseCurrency, targetCurrency)

        then:
        rate == expectedRate
    }

    def "should throw exception if currency pair is not found"() {
        given:
        def baseCurrency = "USD"
        def targetCurrency = "EUR"
        api.getLatestRates(baseCurrency) >> new FreecurrencyapiLatestData([:])

        when:
        currencyExchangeService.getRate(baseCurrency, targetCurrency)

        then:
        thrown(CurrencyPairNotFoundException)
    }

    def "should throw exception if exchange rate api is unavailable"() {
        given:
        def baseCurrency = "USD"
        def targetCurrency = "EUR"
        api.getLatestRates(baseCurrency) >> { throw [502, ""] as FeignException }

        when:
        currencyExchangeService.getRate(baseCurrency, targetCurrency)

        then:
        thrown(CurrencyExchangeRatesUnavailableException)
    }
}
