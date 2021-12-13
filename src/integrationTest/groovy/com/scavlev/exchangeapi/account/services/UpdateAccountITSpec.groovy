package com.scavlev.exchangeapi.account.services

import com.scavlev.exchangeapi.IntegrationSpecification
import com.scavlev.exchangeapi.account.AccountData
import com.scavlev.exchangeapi.account.UpdateAccountRequest
import com.scavlev.exchangeapi.account.domain.AccountRepository
import com.scavlev.exchangeapi.account.domain.AccountStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class UpdateAccountITSpec extends IntegrationSpecification {

    @Autowired
    AccountRepository accountRepository

    @Autowired
    UpdateAccount updateAccount

    @Transactional
    def "should update account record"() {
        given:
        long accountId = 15

        when:
        AccountData updatedAccount = updateAccount.update(accountId, new UpdateAccountRequest(AccountStatus.DEACTIVATED))

        then:
        updatedAccount.status == AccountStatus.DEACTIVATED
        accountRepository.getById(accountId).status == AccountStatus.DEACTIVATED
    }
}
