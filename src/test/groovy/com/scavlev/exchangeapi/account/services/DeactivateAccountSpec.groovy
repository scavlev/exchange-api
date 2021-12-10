package com.scavlev.exchangeapi.account.services

import com.scavlev.exchangeapi.account.AccountNotFoundException
import com.scavlev.exchangeapi.account.domain.Account
import com.scavlev.exchangeapi.account.domain.AccountRepository
import com.scavlev.exchangeapi.client.domain.Client
import spock.lang.Specification

class DeactivateAccountSpec extends Specification {

    AccountRepository accountRepository = Mock()
    DeactivateAccount deactivateAccount = new DeactivateAccount(accountRepository)

    def "should deactivate account"() {
        given:
        def accountId = 1
        Account account = Mock()
        account.client >> Mock(Client)

        accountRepository.findById(accountId) >> Optional.of(account)

        when:
        deactivateAccount.apply(accountId)

        then:
        1 * accountRepository.deactivateAccount(accountId)
    }

    def "should throw exception if account is not found"() {
        given:
        def accountId = 1

        when:
        deactivateAccount.apply(accountId)

        then:
        1 * accountRepository.findById(accountId) >> Optional.empty()
        thrown(AccountNotFoundException)
    }

}
