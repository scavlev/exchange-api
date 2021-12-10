package com.scavlev.exchangeapi.currency;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {SupportedCurrencyValidator.class})
public @interface SupportedCurrency {
    String message() default "Currency is not supported";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
