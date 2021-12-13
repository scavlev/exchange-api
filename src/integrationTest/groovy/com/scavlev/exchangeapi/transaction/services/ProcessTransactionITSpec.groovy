package com.scavlev.exchangeapi.transaction.services


import com.scavlev.exchangeapi.WireMockSpecification
import com.scavlev.exchangeapi.account.domain.AccountEntryType
import com.scavlev.exchangeapi.transaction.ProcessTransactionRequest
import com.scavlev.exchangeapi.transaction.TransactionData
import com.scavlev.exchangeapi.transaction.domain.Transaction
import com.scavlev.exchangeapi.transaction.domain.TransactionRepository
import com.scavlev.exchangeapi.transaction.domain.TransactionType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

import static com.github.tomakehurst.wiremock.client.WireMock.*

class ProcessTransactionITSpec extends WireMockSpecification {

    @Autowired
    TransactionRepository transactionRepository

    @Autowired
    ProcessTransaction processTransaction

    @Transactional
    def "should process exchange transaction request"() {
        given:
        long fromAccountId = 16
        long toAccountId = 17
        BigDecimal amount = 5

        wireMock.stubFor(get(urlPathEqualTo("/api/v2/latest"))
                .willReturn(ok()
                        .withBodyFile("currencies/{{request.query.base_currency}}.json")))

        when:
        TransactionData transactionData = processTransaction.process(new ProcessTransactionRequest(fromAccountId, toAccountId, amount))

        then:
        Transaction transaction = transactionRepository.getById(transactionData.id)
        with(transactionData) {
            id != null
            timestamp != null
            type == TransactionType.EXCHANGE
            exchangeRate.present
            with(exchangeRate.get()) {
                rate == 0.878617
                fromCurrency == "PGK"
                toCurrency == "GEL"
            }
            fromAccount.present
            with(fromAccount.get()) {
                id == fromAccountId
                it.amount == amount.negate()
                currency == "PGK"
            }
            toAccount.present
            with(toAccount.get()) {
                id == toAccountId
                it.amount == 4.39
                currency == "GEL"
            }
        }
        with(transaction) {
            id == transactionData.id
            entityCreated == transactionData.timestamp
            transactionType == TransactionType.EXCHANGE
            exchangeRate.present
            with(exchangeRate.get()) {
                rate == 0.878617
                fromCurrency == "PGK"
                toCurrency == "GEL"
            }
            creditAccountEntry.present
            with(creditAccountEntry.get()) {
                it.account.id == fromAccountId
                it.amount == amount.negate()
                type == AccountEntryType.CREDIT
            }
            debitAccountEntry.present
            with(debitAccountEntry.get()) {
                it.account.id == toAccountId
                it.amount == 4.39
                type == AccountEntryType.DEBIT
            }
        }
    }

    @Transactional
    def "should process transfer transaction request"() {
        given:
        long fromAccountId = 1
        long toAccountId = 18
        BigDecimal amount = 5

        wireMock.stubFor(get(urlPathEqualTo("/api/v2/latest"))
                .willReturn(ok()
                        .withBodyFile("currencies/{{request.query.base_currency}}.json")))

        when:
        TransactionData transactionData = processTransaction.process(new ProcessTransactionRequest(fromAccountId, toAccountId, amount))

        then:
        Transaction transaction = transactionRepository.getById(transactionData.id)
        with(transactionData) {
            id != null
            timestamp != null
            type == TransactionType.TRANSFER
            !exchangeRate.present
            fromAccount.present
            with(fromAccount.get()) {
                id == fromAccountId
                it.amount == amount.negate()
                currency == "UZS"
            }
            toAccount.present
            with(toAccount.get()) {
                id == toAccountId
                it.amount == amount
                currency == "UZS"
            }
        }
        with(transaction) {
            id == transactionData.id
            entityCreated == transactionData.timestamp
            transactionType == TransactionType.TRANSFER
            !exchangeRate.present
            creditAccountEntry.present
            with(creditAccountEntry.get()) {
                it.account.id == fromAccountId
                it.amount == amount.negate()
                type == AccountEntryType.CREDIT
            }
            debitAccountEntry.present
            with(debitAccountEntry.get()) {
                it.account.id == toAccountId
                it.amount == amount
                type == AccountEntryType.DEBIT
            }
        }
    }
}
