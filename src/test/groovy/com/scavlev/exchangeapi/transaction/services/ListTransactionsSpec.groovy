package com.scavlev.exchangeapi.transaction.services

import com.scavlev.exchangeapi.transaction.TransactionData
import com.scavlev.exchangeapi.transaction.domain.TransactionRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import spock.lang.Specification

import static com.scavlev.exchangeapi.FixtureHelper.createTransaction

class ListTransactionsSpec extends Specification {

    TransactionRepository transactionRepository = Mock()
    ListTransactions listTransactions = new ListTransactions(transactionRepository)

    def "should return a page of transaction data"() {
        given:
        def pageRequest = PageRequest.of(0, 10)
        def transactions = (1..10).collect {
            createTransaction(id: it)
        }

        when:
        Page<TransactionData> transactionDataPage = listTransactions.list(pageRequest)

        then:
        1 * transactionRepository.findAll(pageRequest) >> new PageImpl<>(transactions)
        transactionDataPage.size() == transactions.size()
    }

}
