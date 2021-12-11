package com.scavlev.exchangeapi.currency;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@EnableCaching
@Configuration
class CurrencyCacheConfiguration {

    @Bean
    CacheManager currencyCacheManager() {
        Caffeine<Object, Object> caffeineCacheBuilder = Caffeine.newBuilder()
                .maximumSize(500)
                .expireAfterWrite(5, TimeUnit.MINUTES);

        CaffeineCacheManager cacheManager = new CaffeineCacheManager("currencies");
        cacheManager.setCaffeine(caffeineCacheBuilder);
        return cacheManager;
    }

}
