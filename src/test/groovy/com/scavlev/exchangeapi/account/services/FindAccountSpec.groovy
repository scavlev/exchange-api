package com.scavlev.exchangeapi.account.services

import com.scavlev.exchangeapi.account.AccountData
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
        long accountId = 1

        when:
        Optional<AccountData> accountData = findAccount.find(accountId)

        then:
        1 * accountRepository.findById(accountId) >> Optional.empty()
        accountData.isEmpty()
    }

    def "should return account data if account is found"() {
        given:
        long accountId = 1
        Account account = createAccount(id: accountId)

        when:
        Optional<AccountData> accountData = findAccount.find(accountId)

        then:
        1 * accountRepository.findById(accountId) >> Optional.of(account)
        accountData.present
        accountData.get() == fromAccount(account)
    }

}
