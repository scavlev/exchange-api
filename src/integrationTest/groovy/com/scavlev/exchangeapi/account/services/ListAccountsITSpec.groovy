package com.scavlev.exchangeapi.account.services

import com.scavlev.exchangeapi.IntegrationSpecification
import com.scavlev.exchangeapi.account.AccountData
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest

class ListAccountsITSpec extends IntegrationSpecification {

    @Autowired
    ListAccounts listAccounts

    def "should return a page of accounts"() {
        when:
        Page<AccountData> pageOfAccounts = listAccounts.list(PageRequest.of(0, 5))

        then:
        pageOfAccounts.numberOfElements == 5
        pageOfAccounts.totalElements >= 30
    }
}
