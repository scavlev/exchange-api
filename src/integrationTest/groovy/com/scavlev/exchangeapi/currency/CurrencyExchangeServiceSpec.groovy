package com.scavlev.exchangeapi.currency


import com.scavlev.exchangeapi.WireMockSpecification
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Unroll

import static com.github.tomakehurst.wiremock.client.WireMock.*

class CurrencyExchangeServiceSpec extends WireMockSpecification {

    @Autowired
    CurrencyExchangeService currencyExchangeService

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

}
