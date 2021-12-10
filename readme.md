# exchange-api

## :rocket: Run with demo data

```
# With demo data
./gradlew bootRun --args='--spring.profiles.active=demo'

# Without demo data
./gradlew bootRun
```

> :warning: Running it with `demo` profile initiates the database with some initial demo data.

## :book: Api docs

Swagger docs are available at http://localhost:8080/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config

## :money_with_wings: Conversion rates

Service uses https://freecurrencyapi.net api to get currency conversion pairs.

Accounts with currencies other than ones listed on https://freecurrencyapi.net are not supported.

> :warning: Api has request amount limitations, so it might become unresponsive at some point.

## :mag_right: Testing

```
# All tests
./gradlew check 

# Unit tests only
./gradlew test 

# Integration tests only
./gradlew integrationTest 
```