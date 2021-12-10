package com.scavlev.exchangeapi.currency;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static java.lang.String.format;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
class SupportedCurrencyValidator implements ConstraintValidator<SupportedCurrency, String> {

    private final SupportedCurrencies supportedCurrencies;

    @Override
    public boolean isValid(String currency, ConstraintValidatorContext context) {

        boolean isValid = supportedCurrencies.getCurrencies().contains(currency.toUpperCase());

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(format("Currency %s is not supported", currency))
                    .addConstraintViolation();
        }

        return isValid;
    }

}
