package com.scavlev.exchangeapi.account.domain

import com.scavlev.exchangeapi.IntegrationSpecification
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest

class AccountEntryRepositoryITSpec extends IntegrationSpecification {

    @Autowired
    AccountEntryRepository accountEntryRepository

    def "should find account entries for account"() {
        when:
        Page<AccountEntry> accountEntryPage = accountEntryRepository.findByAccountId(1, PageRequest.of(0, 10))

        then:
        accountEntryPage.numberOfElements == 6
        accountEntryPage.stream().allMatch({ it.account.id == 1 })
    }
}
