package com.scavlev.exchangeapi.currency;

import feign.Feign;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class FreecurrencyapiConfiguration {

    private final String apiKey;
    private final String apiUrl;

    FreecurrencyapiConfiguration(@Value("${app.freecurrencyapi.api_key}") String apiKey,
                                 @Value("${app.freecurrencyapi.url}") String apiUrl) {
        this.apiKey = apiKey;
        this.apiUrl = apiUrl;
    }

    @Bean
    FreecurrencyapiApi freecurrencyapiApi() {
        return Feign.builder()
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .requestInterceptor(new FreecurrencyapiApiInterceptor(apiKey))
                .target(FreecurrencyapiApi.class, apiUrl);
    }

    @AllArgsConstructor
    private static class FreecurrencyapiApiInterceptor implements RequestInterceptor {

        private final String apiKey;

        @Override
        public void apply(RequestTemplate template) {
            if (!template.queries().containsKey("apikey")) {
                template.query("apikey", apiKey);
            }
        }
    }

}
