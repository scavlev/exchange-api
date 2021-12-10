package com.scavlev.exchangeapi.account

import com.scavlev.exchangeapi.account.domain.Account
import com.scavlev.exchangeapi.account.domain.AccountStatus
import com.scavlev.exchangeapi.client.domain.Client
import spock.lang.Specification

class AccountDataSpec extends Specification {

    def "should correctly transform account data"() {
        given:
        Client client = Mock()
        client.id >> 11

        Account account = Mock()
        account.id >> 13
        account.client >> client
        account.status >> AccountStatus.ACTIVE
        account.balance >> 54.33
        account.currency >> "EUR"

        when:
        AccountData accountData = AccountData.fromAccount(account)

        then:
        accountData != null
        with(accountData) {
            id == account.id
            clientId == client.id
            status == account.status
            balance == account.balance
            currency == account.currency
        }
    }

}
