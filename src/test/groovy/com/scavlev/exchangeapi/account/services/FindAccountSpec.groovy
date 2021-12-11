package com.scavlev.exchangeapi.account.services

import com.scavlev.exchangeapi.account.domain.Account
import com.scavlev.exchangeapi.account.domain.AccountRepository
import spock.lang.Specification

import static com.scavlev.exchangeapi.FixtureHelper.createAccount
import static com.scavlev.exchangeapi.account.AccountData.fromAccount

class FindAccountSpec extends Specification {

    AccountRepository accountRepository = Mock()
    FindAccount findAccount = new FindAccount(accountRepository)

    def "should return empty optional if account is not found"() {
        given:
        def accountId = 1

        when:
        def accountData = findAccount.apply(accountId)

        then:
        1 * accountRepository.findById(accountId) >> Optional.empty()
        accountData.isEmpty()
    }

    def "should return account data if account is found"() {
        given:
        def accountId = 1
        Account account = createAccount(id: accountId)

        when:
        def accountData = findAccount.apply(accountId)

        then:
        1 * accountRepository.findById(accountId) >> Optional.of(account)
        accountData.present
        accountData.get() == fromAccount(account)
    }

}
