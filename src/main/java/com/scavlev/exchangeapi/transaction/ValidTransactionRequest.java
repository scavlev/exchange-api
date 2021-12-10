package com.scavlev.exchangeapi.transaction;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Target(TYPE)
@Retention(RUNTIME)
@Constraint(validatedBy = {SameAccountTransactionValidator.class})
public @interface ValidTransactionRequest {

    String message() default "Invalid transaction request";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
