package com.scavlev.exchangeapi.account.services

import com.scavlev.exchangeapi.account.AccountNotFoundException
import com.scavlev.exchangeapi.account.domain.Account
import com.scavlev.exchangeapi.account.domain.AccountRepository
import spock.lang.Specification

import static com.scavlev.exchangeapi.FixtureHelper.createAccount
import static com.scavlev.exchangeapi.account.AccountData.fromAccount

class DeactivateAccountSpec extends Specification {

    AccountRepository accountRepository = Mock()
    DeactivateAccount deactivateAccount = new DeactivateAccount(accountRepository)

    def "should deactivate account"() {
        given:
        def accountId = 1
        Account account = createAccount(id: accountId)

        when:
        def deactivatedAccountData = deactivateAccount.deactivate(accountId)

        then:
        1 * accountRepository.findById(accountId) >> Optional.of(account)
        1 * accountRepository.deactivateAccount(accountId)
        deactivatedAccountData == fromAccount(account)
    }

    def "should throw exception if account is not found"() {
        given:
        def accountId = 1

        when:
        deactivateAccount.deactivate(accountId)

        then:
        1 * accountRepository.findById(accountId) >> Optional.empty()
        thrown(AccountNotFoundException)
    }

}
