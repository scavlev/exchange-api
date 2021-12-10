package com.scavlev.exchangeapi.transaction


import spock.lang.Specification

import javax.validation.ConstraintValidatorContext

class SameAccountTransactionValidatorSpec extends Specification {

    SameAccountTransactionValidator validator = new SameAccountTransactionValidator()

    def "should return false if to and from accounts are the same"() {
        given:
        ProcessTransactionRequest processTransactionRequest = Mock()
        processTransactionRequest.fromAccount >> 1
        processTransactionRequest.toAccount >> 1
        ConstraintValidatorContext context = Mock()

        when:
        def result = validator.isValid(processTransactionRequest, context)

        then:
        !result
        1 * context.disableDefaultConstraintViolation()
        1 * context.buildConstraintViolationWithTemplate(_) >> Mock(ConstraintValidatorContext.ConstraintViolationBuilder)
    }

    def "should return true if to and from accounts are different"() {
        given:
        ProcessTransactionRequest processTransactionRequest = Mock()
        processTransactionRequest.fromAccount >> 1
        processTransactionRequest.toAccount >> 2
        ConstraintValidatorContext context = Mock()

        when:
        def result = validator.isValid(processTransactionRequest, context)

        then:
        result
    }

}
