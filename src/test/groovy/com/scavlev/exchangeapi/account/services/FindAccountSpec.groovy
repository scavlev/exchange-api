package com.scavlev.exchangeapi.account.services

import com.scavlev.exchangeapi.account.domain.Account
import com.scavlev.exchangeapi.account.domain.AccountRepository
import com.scavlev.exchangeapi.account.domain.AccountStatus
import com.scavlev.exchangeapi.client.domain.Client
import spock.lang.Specification

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
        Client client = Mock()
        client.id >> 11

        Account account = Mock()
        account.id >> 13
        account.client >> client
        account.status >> AccountStatus.ACTIVE
        account.balance >> 54.33
        account.currency >> "EUR"

        when:
        def accountData = findAccount.apply(accountId)

        then:
        1 * accountRepository.findById(accountId) >> Optional.of(account)
        accountData.present
        with(accountData.get()) {
            id == account.id
            clientId == client.id
            status == account.status
            balance == account.balance
            currency == account.currency
        }
    }

}
