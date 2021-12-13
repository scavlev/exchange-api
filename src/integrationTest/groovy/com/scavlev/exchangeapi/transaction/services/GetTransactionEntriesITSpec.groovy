package com.scavlev.exchangeapi.transaction.services

import com.scavlev.exchangeapi.IntegrationSpecification
import com.scavlev.exchangeapi.account.AccountEntryData
import com.scavlev.exchangeapi.account.domain.AccountEntryType
import org.springframework.beans.factory.annotation.Autowired

import java.time.OffsetDateTime

class GetTransactionEntriesITSpec extends IntegrationSpecification {

    @Autowired
    GetTransactionEntries getTransactionEntries

    def "should get all transaction entries"() {
        given:
        long transactionId = 7

        when:
        List<AccountEntryData> transactionEntriesPage = getTransactionEntries.get(transactionId)

        then:
        transactionEntriesPage.size() == 1
        with(transactionEntriesPage.first()) {
            accountId != null
            it.transactionId == transactionId
            timestamp.isEqual(OffsetDateTime.parse('2021-12-13T00:30:36.022141Z'))
            amount == 100
            currency == "TWD"
            type == AccountEntryType.DEBIT
        }
    }
}
