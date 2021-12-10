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

    @Value("${app.freecurrencyapi.api_key}")
    private String apiKey;

    @Value("${app.freecurrencyapi.url}")
    private String apiUrl;

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
