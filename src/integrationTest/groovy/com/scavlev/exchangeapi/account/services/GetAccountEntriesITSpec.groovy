package com.scavlev.exchangeapi.account.services

import com.scavlev.exchangeapi.IntegrationSpecification
import com.scavlev.exchangeapi.account.AccountEntryData
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest

class GetAccountEntriesITSpec extends IntegrationSpecification {

    @Autowired
    GetAccountEntries getAccountEntries

    def "should get all account entries"() {
        given:
        long accountId = 7

        when:
        Page<AccountEntryData> accountEntriesPage = getAccountEntries.get(accountId, PageRequest.of(0, 10))

        then:
        accountEntriesPage.numberOfElements == 10
        accountEntriesPage.totalElements >= 11
        accountEntriesPage.stream().allMatch({ it.accountId == accountId })
    }
}
