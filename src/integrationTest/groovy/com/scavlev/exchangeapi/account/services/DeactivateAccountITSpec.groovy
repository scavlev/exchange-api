package com.scavlev.exchangeapi.account.services

import com.scavlev.exchangeapi.IntegrationSpecification
import com.scavlev.exchangeapi.account.AccountData
import com.scavlev.exchangeapi.account.domain.AccountRepository
import com.scavlev.exchangeapi.account.domain.AccountStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class DeactivateAccountITSpec extends IntegrationSpecification {

    @Autowired
    AccountRepository accountRepository

    @Autowired
    DeactivateAccount deactivateAccount

    @Transactional
    def "should deactivate account by id"() {
        given:
        long accountId = 5

        when:
        AccountData deactivatedAccount = deactivateAccount.deactivate(accountId)

        then:
        deactivatedAccount.status == AccountStatus.DEACTIVATED
        accountRepository.getById(accountId).status == AccountStatus.DEACTIVATED
    }
}
