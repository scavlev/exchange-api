package com.scavlev.exchangeapi.currency;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableCaching
@Configuration
class CurrencyCacheConfiguration {

    private final String cacheSpec;

    CurrencyCacheConfiguration(@Value("${app.freecurrencyapi.cache-spec:maximumSize=500, expireAfterWrite=5m}") String cacheSpec) {
        this.cacheSpec = cacheSpec;
    }

    @Bean
    CacheManager currencyCacheManager() {
        Caffeine<Object, Object> caffeineCacheBuilder = Caffeine.from(cacheSpec);

        CaffeineCacheManager cacheManager = new CaffeineCacheManager("currencies");
        cacheManager.setCaffeine(caffeineCacheBuilder);
        return cacheManager;
    }

}
