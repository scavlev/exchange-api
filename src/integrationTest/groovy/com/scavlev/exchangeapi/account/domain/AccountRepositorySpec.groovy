package com.scavlev.exchangeapi.account.domain

import com.scavlev.exchangeapi.IntegrationSpecification
import com.scavlev.exchangeapi.client.domain.ClientRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class AccountRepositorySpec extends IntegrationSpecification {

    @Autowired
    AccountRepository accountRepository
    @Autowired
    ClientRepository clientRepository

    @Transactional
    def "should deactivate account by id"() {
        given:
        def accountId = 2L

        when:
        accountRepository.deactivateAccount(accountId)

        then:
        accountRepository.getById(accountId).status == AccountStatus.DEACTIVATED
    }

    @Transactional
    def "should deactivate all client accounts by id"() {
        given:
        def clientId = 2L

        when:
        accountRepository.deactivateClientAccounts(clientId)

        then:
        clientRepository.getById(clientId).accounts.stream().allMatch({
            it.status == AccountStatus.DEACTIVATED
        })
    }

}
