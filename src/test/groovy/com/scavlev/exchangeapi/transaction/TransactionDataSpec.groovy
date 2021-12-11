package com.scavlev.exchangeapi.transaction

import com.scavlev.exchangeapi.account.domain.AccountEntryType
import com.scavlev.exchangeapi.transaction.domain.Transaction
import spock.lang.Specification

import static com.scavlev.exchangeapi.FixtureHelper.*

class TransactionDataSpec extends Specification {

    def "should correctly transform transaction data"() {
        given:
        Transaction transaction = createTransaction().with {
            entries.addAll(
                    createAccountEntry(id: 1, transaction: it, type: AccountEntryType.DEBIT),
                    createAccountEntry(id: 2, transaction: it, type: AccountEntryType.CREDIT)
            )
            it.exchangeRate = createExchangeRate(transaction: it)
            it
        }

        when:
        TransactionData transactionData = TransactionData.fromTransaction(transaction)

        then:
        transactionData != null
        with(transactionData) {
            id == transaction.id
            type == transaction.transactionType
            timestamp == transaction.entityCreated

            toAccount.present
            toAccount.get().with {
                id == transaction.entries[0].account.id
                amount == transaction.entries[0].amount
                currency == transaction.entries[0].account.currency
            }

            fromAccount.present
            fromAccount.get().with {
                id == transaction.entries[1].account.id
                amount == transaction.entries[1].amount
                currency == transaction.entries[1].account.currency
            }

            exchangeRate.present
            exchangeRate.get().with {
                fromCurrency == transaction.entries[1].account.currency
                toCurrency == transaction.entries[0].account.currency
                rate == transaction.exchangeRate.get().rate
            }
        }
    }

}
