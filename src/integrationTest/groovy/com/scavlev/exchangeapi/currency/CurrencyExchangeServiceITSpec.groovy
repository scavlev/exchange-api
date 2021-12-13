package com.scavlev.exchangeapi.currency

import com.scavlev.exchangeapi.WireMockSpecification
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.Cache
import org.springframework.cache.CacheManager
import org.springframework.cache.interceptor.SimpleKey
import spock.lang.Unroll

import static com.github.tomakehurst.wiremock.client.WireMock.*

class CurrencyExchangeServiceITSpec extends WireMockSpecification {

    @Autowired
    CurrencyExchangeService currencyExchangeService

    @Autowired
    CacheManager cacheManager

    @Unroll
    def "should call currency rate api and return proper rate of #expectedRate for currency pair #baseCurrency/#targetCurrency"() {
        given:
        wireMock.stubFor(get(urlPathEqualTo("/api/v2/latest"))
                .willReturn(ok()
                        .withBodyFile("currencies/{{request.query.base_currency}}.json")))

        when:
        BigDecimal rate = currencyExchangeService.getRate(baseCurrency, targetCurrency)

        then:
        rate == expectedRate

        where:
        baseCurrency | targetCurrency | expectedRate
        "EUR"        | "USD"          | 1.134803
        "USD"        | "EUR"          | 0.88121
        "DKK"        | "USD"          | 0.152583
        "USD"        | "DKK"          | 6.55381
        "EUR"        | "DKK"          | 7.437285
        "DKK"        | "EUR"          | 0.134458
    }

    def "should throw exception if currency pair is not found"() {
        given:
        wireMock.stubFor(get(urlPathEqualTo("/api/v2/latest"))
                .willReturn(ok()
                        .withBodyFile("currencies/{{request.query.base_currency}}.json")))

        when:
        currencyExchangeService.getRate("EUR", "WHAT")

        then:
        thrown(CurrencyPairNotFoundException)
    }

    def "should throw exception if currency api is unavailable"() {
        given:
        wireMock.stubFor(get(urlPathEqualTo("/api/v2/latest"))
                .willReturn(serviceUnavailable()))

        when:
        currencyExchangeService.getRate("EUR", "WHAT")

        then:
        thrown(CurrencyExchangeRatesUnavailableException)
    }

    def "should cache currency exchange rates"() {
        given:
        String baseCurrency = "BTC"
        String targetCurrency = "XRP"
        wireMock.stubFor(get(urlPathEqualTo("/api/v2/latest"))
                .willReturn(ok()
                        .withBodyFile("currencies/{{request.query.base_currency}}.json")))

        Cache cache = cacheManager.getCache('currencies')
        SimpleKey cacheKey = new SimpleKey(baseCurrency, targetCurrency)
        cache.get(cacheKey) == null

        when:
        BigDecimal rate = currencyExchangeService.getRate(baseCurrency, targetCurrency)

        then:
        rate == cache.get(cacheKey).get()
    }

}
