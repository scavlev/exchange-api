package com.scavlev.exchangeapi.transaction

import com.scavlev.exchangeapi.account.domain.Account
import com.scavlev.exchangeapi.account.domain.AccountEntry
import com.scavlev.exchangeapi.transaction.domain.ExchangeRate
import com.scavlev.exchangeapi.transaction.domain.Transaction
import com.scavlev.exchangeapi.transaction.domain.TransactionType
import spock.lang.Specification

import java.time.LocalDateTime

class TransactionDataSpec extends Specification {

    def "should correctly transform transaction data"() {
        given:
        Account fromAccount = Mock()
        fromAccount.id >> 1
        fromAccount.currency >> "USD"

        Account toAccount = Mock()
        toAccount.id >> 2
        toAccount.currency >> "EUR"

        AccountEntry fromAccountEntry = Mock()
        fromAccountEntry.amount >> 20
        fromAccountEntry.account >> fromAccount

        AccountEntry toAccountEntry = Mock()
        toAccountEntry.amount >> 10
        toAccountEntry.account >> toAccount

        ExchangeRate exchangeRate = Mock()
        exchangeRate.fromCurrency >> fromAccount.currency
        exchangeRate.toCurrency >> toAccount.currency
        exchangeRate.rate >> 0.5

        Transaction transaction = Mock()
        transaction.id >> 42
        transaction.transactionType >> TransactionType.EXCHANGE
        transaction.entityCreated >> LocalDateTime.now()
        transaction.exchangeRate >> Optional.of(exchangeRate)
        transaction.debitAccountEntry >> Optional.of(toAccountEntry)
        transaction.creditAccountEntry >> Optional.of(fromAccountEntry)

        when:
        TransactionData transactionData = TransactionData.fromTransaction(transaction)

        then:
        transactionData != null
        with(transactionData) {
            id == transaction.id
            type == transaction.transactionType
            timestamp == transaction.entityCreated
            it.toAccount.present
            it.toAccount.get().amount == toAccountEntry.amount
            it.toAccount.get().id == toAccount.id
            it.toAccount.get().currency == toAccount.currency
            it.fromAccount.present
            it.fromAccount.get().amount == fromAccountEntry.amount
            it.fromAccount.get().id == fromAccount.id
            it.fromAccount.get().currency == fromAccount.currency
            it.exchangeRate.present
            it.exchangeRate.get().fromCurrency == fromAccount.currency
            it.exchangeRate.get().toCurrency == toAccount.currency
            it.exchangeRate.get().rate == exchangeRate.rate
        }
    }

}
