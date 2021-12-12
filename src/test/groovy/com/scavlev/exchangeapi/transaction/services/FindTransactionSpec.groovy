package com.scavlev.exchangeapi.transaction.services

import com.scavlev.exchangeapi.transaction.domain.Transaction
import com.scavlev.exchangeapi.transaction.domain.TransactionRepository
import spock.lang.Specification

import static com.scavlev.exchangeapi.FixtureHelper.createTransaction
import static com.scavlev.exchangeapi.transaction.TransactionData.fromTransaction

class FindTransactionSpec extends Specification {

    TransactionRepository transactionRepository = Mock()
    FindTransaction findTransaction = new FindTransaction(transactionRepository)

    def "should return empty optional if transaction is not found"() {
        given:
        def transactionId = 1

        when:
        def transactionData = findTransaction.find(transactionId)

        then:
        1 * transactionRepository.findById(transactionId) >> Optional.empty()
        transactionData.isEmpty()
    }

    def "should return transaction data if transaction is found"() {
        given:
        def transactionId = 1
        Transaction transaction = createTransaction()

        when:
        def transactionData = findTransaction.find(transactionId)

        then:
        1 * transactionRepository.findById(transactionId) >> Optional.of(transaction)
        transactionData.present
        transactionData.get() == fromTransaction(transaction)
    }

}
