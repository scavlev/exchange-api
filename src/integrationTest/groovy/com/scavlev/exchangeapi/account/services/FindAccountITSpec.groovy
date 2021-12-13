package com.scavlev.exchangeapi.account.services

import com.scavlev.exchangeapi.IntegrationSpecification
import com.scavlev.exchangeapi.account.AccountData
import com.scavlev.exchangeapi.account.domain.AccountStatus
import org.springframework.beans.factory.annotation.Autowired

class FindAccountITSpec extends IntegrationSpecification {

    @Autowired
    FindAccount findAccount

    def "should find an account by id"() {
        given:
        long accountId = 10

        when:
        Optional<AccountData> accountData = findAccount.find(accountId)

        then:
        accountData.present
        with(accountData.get()) {
            id == accountId
            clientId == 4L
            balance == 108.76
            currency == "DOP"
            status == AccountStatus.ACTIVE
        }
    }
}
