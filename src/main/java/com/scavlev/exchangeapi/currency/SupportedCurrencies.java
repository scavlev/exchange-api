package com.scavlev.exchangeapi.currency;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "app")
public class SupportedCurrencies {

    @Getter
    private final List<String> currencies = new ArrayList<>();

}
