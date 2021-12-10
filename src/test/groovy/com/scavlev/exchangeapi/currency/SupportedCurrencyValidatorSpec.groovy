package com.scavlev.exchangeapi.currency


import spock.lang.Specification

import javax.validation.ConstraintValidatorContext

class SupportedCurrencyValidatorSpec extends Specification {

    SupportedCurrencies supportedCurrencies = Mock()
    SupportedCurrencyValidator validator = new SupportedCurrencyValidator(supportedCurrencies)

    def "should return false if currency is not on supported list"() {
        given:
        def currency = "DOGE"
        supportedCurrencies.currencies >> ["USD", "EUR"]
        ConstraintValidatorContext context = Mock()

        when:
        def result = validator.isValid(currency, context)

        then:
        !result
        1 * context.disableDefaultConstraintViolation()
        1 * context.buildConstraintViolationWithTemplate(_) >> Mock(ConstraintValidatorContext.ConstraintViolationBuilder)
    }

    def "should return true if currency is on supported list"() {
        given:
        def currency = "EUR"
        supportedCurrencies.currencies >> ["USD", "EUR"]
        ConstraintValidatorContext context = Mock()

        when:
        def result = validator.isValid(currency, context)

        then:
        result
    }

}