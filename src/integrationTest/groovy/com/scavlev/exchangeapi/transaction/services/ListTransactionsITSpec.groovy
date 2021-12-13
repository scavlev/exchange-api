package com.scavlev.exchangeapi.transaction.services

import com.scavlev.exchangeapi.IntegrationSpecification
import com.scavlev.exchangeapi.transaction.TransactionData
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest

class ListTransactionsITSpec extends IntegrationSpecification {

    @Autowired
    ListTransactions listTransactions

    def "should return a page of transactions"() {
        when:
        Page<TransactionData> pageOfTransactions = listTransactions.list(PageRequest.of(0, 5))

        then:
        pageOfTransactions.numberOfElements == 5
        pageOfTransactions.totalElements >= 165
    }
}
