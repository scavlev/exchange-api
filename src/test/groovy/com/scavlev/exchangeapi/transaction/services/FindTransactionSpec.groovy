package com.scavlev.exchangeapi.transaction.services

import com.scavlev.exchangeapi.transaction.domain.Transaction
import com.scavlev.exchangeapi.transaction.domain.TransactionRepository
import spock.lang.Specification

class FindTransactionSpec extends Specification {

    TransactionRepository transactionRepository = Mock()
    FindTransaction findTransaction = new FindTransaction(transactionRepository)

    def "should return empty optional if transaction is not found"() {
        given:
        def transactionId = 1

        when:
        def transactionData = findTransaction.apply(transactionId)

        then:
        1 * transactionRepository.findById(transactionId) >> Optional.empty()
        transactionData.isEmpty()
    }

    def "should return transaction data if transaction is found"() {
        given:
        def transactionId = 1
        Transaction transaction = Mock()
        transaction.id >> transactionId
        transaction.exchangeRate >> Optional.empty()
        transaction.debitAccountEntry >> Optional.empty()
        transaction.creditAccountEntry >> Optional.empty()

        when:
        def transactionData = findTransaction.apply(transactionId)

        then:
        1 * transactionRepository.findById(transactionId) >> Optional.of(transaction)
        transactionData.present
        transactionData.get().id == transactionId
    }

}
