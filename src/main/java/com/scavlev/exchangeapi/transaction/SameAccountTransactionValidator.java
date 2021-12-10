package com.scavlev.exchangeapi.transaction;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

class SameAccountTransactionValidator implements ConstraintValidator<ValidTransactionRequest, ProcessTransactionRequest> {

    @Override
    public boolean isValid(ProcessTransactionRequest processTransactionRequest, ConstraintValidatorContext context) {

        boolean isSameAccountTransfer = processTransactionRequest.getFromAccount().equals(processTransactionRequest.getToAccount());

        if (isSameAccountTransfer) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Can't transfer funds to the same account")
                    .addConstraintViolation();
        }

        return !isSameAccountTransfer;
    }

}
