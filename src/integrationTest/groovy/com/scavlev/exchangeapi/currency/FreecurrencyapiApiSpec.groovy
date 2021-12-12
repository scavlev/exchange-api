package com.scavlev.exchangeapi.currency

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.scavlev.exchangeapi.WireMockSpecification
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value

import static com.github.tomakehurst.wiremock.client.WireMock.*

class FreecurrencyapiApiSpec extends WireMockSpecification {

    @Autowired
    FreecurrencyapiApi freecurrencyapiApi

    @Value('${app.freecurrencyapi.api_key}')
    String apiKey

    def "should call currency rate api and deserialize the response"() {
        given:
        wireMock.stubFor(get(urlPathEqualTo("/api/v2/latest"))
                .willReturn(ok()
                        .withBodyFile("currencies/{{request.query.base_currency}}.json")))

        when:
        FreecurrencyapiLatestData freecurrencyapiLatestData = freecurrencyapiApi.getLatestRates(baseCurrency)

        then:
        freecurrencyapiLatestData == objectMapper().readValue(currencyResponseFile(baseCurrency), FreecurrencyapiLatestData)
        wireMock.verify(1, getRequestedFor(urlPathEqualTo("/api/v2/latest"))
                .withQueryParam("base_currency", equalTo(baseCurrency))
                .withQueryParam("apikey", equalTo(apiKey)))

        where:
        baseCurrency | _
        "EUR"        | _
        "USD"        | _
        "DKK"        | _
    }

    static def currencyResponseFile(String currencyCode) {
        new File("src/integrationTest/resources/__files/currencies/${currencyCode}.json")
    }

    static def objectMapper() {
        new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }
}
