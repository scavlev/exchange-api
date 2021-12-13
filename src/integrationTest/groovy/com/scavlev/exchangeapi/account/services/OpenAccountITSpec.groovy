package com.scavlev.exchangeapi.account.services

import com.scavlev.exchangeapi.IntegrationSpecification
import com.scavlev.exchangeapi.account.AccountData
import com.scavlev.exchangeapi.account.OpenAccountRequest
import com.scavlev.exchangeapi.account.domain.Account
import com.scavlev.exchangeapi.account.domain.AccountRepository
import com.scavlev.exchangeapi.account.domain.AccountStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class OpenAccountITSpec extends IntegrationSpecification {

    @Autowired
    AccountRepository accountRepository

    @Autowired
    OpenAccount openAccount

    @Transactional
    def "should successfully create a new account"() {
        when:
        Long clientId = 6
        String currency = "BTC"
        AccountData openedAccount = openAccount.open(new OpenAccountRequest(clientId, currency))

        then:
        Account dbAccount = accountRepository.getById(openedAccount.id)
        with(openedAccount) {
            id != null
            it.clientId == clientId
            balance == 0.0
            it.currency == currency
            status == AccountStatus.ACTIVE
        }
        with(dbAccount) {
            id == openedAccount.id
            client.id == clientId
            balance == 0.0
            it.currency == currency
            status == AccountStatus.ACTIVE
        }
    }
}
