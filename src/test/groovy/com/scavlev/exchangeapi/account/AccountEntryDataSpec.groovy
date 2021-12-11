package com.scavlev.exchangeapi.account


import com.scavlev.exchangeapi.account.domain.AccountEntry
import spock.lang.Specification

import static com.scavlev.exchangeapi.FixtureHelper.createAccountEntry

class AccountEntryDataSpec extends Specification {

    def "should correctly transform account entry data"() {
        given:
        AccountEntry entry = createAccountEntry()

        when:
        AccountEntryData accountEntryData = AccountEntryData.fromAccountEntry(entry)

        then:
        accountEntryData != null
        with(accountEntryData) {
            accountId == entry.account.id
            transactionId == entry.transaction.id
            timestamp == entry.entityCreated
            amount == entry.amount
            currency == entry.account.currency
            type == entry.type
        }
    }

}