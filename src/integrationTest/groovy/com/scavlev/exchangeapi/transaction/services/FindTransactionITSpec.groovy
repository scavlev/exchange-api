package com.scavlev.exchangeapi.transaction.services

import com.scavlev.exchangeapi.IntegrationSpecification
import com.scavlev.exchangeapi.transaction.TransactionData
import com.scavlev.exchangeapi.transaction.domain.TransactionType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

import java.time.OffsetDateTime

class FindTransactionITSpec extends IntegrationSpecification {

    @Autowired
    FindTransaction findTransaction

    @Transactional
    def "should find transaction by id"() {
        given:
        long transactionId = 22

        when:
        Optional<TransactionData> transactionData = findTransaction.find(transactionId)

        then:
        transactionData.present
        with(transactionData.get()) {
            id == transactionId
            timestamp.isEqual(OffsetDateTime.parse('2021-12-13T00:30:36.070807Z'))
            type == TransactionType.DEPOSIT
            !exchangeRate.present
            !fromAccount.present
            toAccount.present
            with(toAccount.get()) {
                id == 22
                it.amount == 100
                currency == "GHS"
            }
        }
    }
}
