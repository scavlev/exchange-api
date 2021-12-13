package com.scavlev.exchangeapi.api;

import com.scavlev.exchangeapi.account.AccountEntriesRetrievalException;
import com.scavlev.exchangeapi.account.AccountNotFoundException;
import com.scavlev.exchangeapi.account.DeactivatedAccountAccessException;
import com.scavlev.exchangeapi.client.ClientDoesntExistException;
import com.scavlev.exchangeapi.client.ClientNotFoundException;
import com.scavlev.exchangeapi.currency.CurrencyExchangeRatesUnavailableException;
import com.scavlev.exchangeapi.currency.CurrencyPairNotFoundException;
import com.scavlev.exchangeapi.transaction.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.time.ZonedDateTime;
import java.util.stream.Collectors;

import static java.lang.String.format;

@ControllerAdvice
class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({
            ClientNotFoundException.class,
            AccountNotFoundException.class,
            TransactionNotFoundException.class
    })
    ResponseEntity<ApiError> handleNotFound(RuntimeException ex, HttpServletRequest httpRequest) {
        return new ResponseEntity<>(
                apiError(ex.getMessage(), HttpStatus.NOT_FOUND, httpRequest.getRequestURI()),
                new HttpHeaders(),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({
            DeactivatedAccountAccessException.class,
            OperationOnNonExistentAccountException.class,
            TransactionEntryLoadException.class,
            InsufficientFundsException.class,
            AccountEntriesRetrievalException.class,
            ClientDoesntExistException.class
    })
    ResponseEntity<ApiError> handleBadRequest(RuntimeException ex, HttpServletRequest httpRequest) {
        return new ResponseEntity<>(
                apiError(ex.getMessage(), HttpStatus.BAD_REQUEST, httpRequest.getRequestURI()),
                new HttpHeaders(),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({
            CurrencyPairNotFoundException.class,
            InvalidReceivableAmount.class
    })
    ResponseEntity<ApiError> handleConflict(RuntimeException ex, HttpServletRequest httpRequest) {
        return new ResponseEntity<>(
                apiError(ex.getMessage(), HttpStatus.CONFLICT, httpRequest.getRequestURI()),
                new HttpHeaders(),
                HttpStatus.CONFLICT);
    }

    @ExceptionHandler({
            CurrencyExchangeRatesUnavailableException.class,
    })
    ResponseEntity<ApiError> handleUnavailable(RuntimeException ex, HttpServletRequest httpRequest) {
        return new ResponseEntity<>(
                apiError(ex.getMessage(), HttpStatus.SERVICE_UNAVAILABLE, httpRequest.getRequestURI()),
                new HttpHeaders(),
                HttpStatus.SERVICE_UNAVAILABLE);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String errors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(error -> error instanceof FieldError
                        ? format("%s %s", ((FieldError) error).getField(), error.getDefaultMessage())
                        : error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return new ResponseEntity<>(
                apiError(errors, HttpStatus.BAD_REQUEST, ((ServletWebRequest) request).getRequest().getRequestURI()),
                new HttpHeaders(),
                HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return new ResponseEntity<>(
                apiError(ex.getMessage(), HttpStatus.BAD_REQUEST, ((ServletWebRequest) request).getRequest().getRequestURI()),
                new HttpHeaders(),
                HttpStatus.BAD_REQUEST);
    }

    private ApiError apiError(String error, HttpStatus status, String uri) {
        return ApiError.builder()
                .timestamp(ZonedDateTime.now())
                .status(status.value())
                .error(error)
                .path(uri)
                .build();
    }

}
