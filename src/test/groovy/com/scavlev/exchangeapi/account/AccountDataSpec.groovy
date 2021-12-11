package com.scavlev.exchangeapi.account

import com.scavlev.exchangeapi.account.domain.Account
import spock.lang.Specification

import static com.scavlev.exchangeapi.FixtureHelper.createAccount

class AccountDataSpec extends Specification {

    def "should correctly transform account data"() {
        given:
        Account account = createAccount()

        when:
        AccountData accountData = AccountData.fromAccount(account)

        then:
        accountData != null
        with(accountData) {
            id == account.id
            clientId == account.client.id
            status == account.status
            balance == account.balance
            currency == account.currency
        }
    }

}
