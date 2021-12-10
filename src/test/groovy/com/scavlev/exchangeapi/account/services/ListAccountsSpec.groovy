package com.scavlev.exchangeapi.account.services

import com.scavlev.exchangeapi.account.AccountData
import com.scavlev.exchangeapi.account.domain.Account
import com.scavlev.exchangeapi.account.domain.AccountRepository
import com.scavlev.exchangeapi.client.domain.Client
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import spock.lang.Specification

class ListAccountsSpec extends Specification {

    AccountRepository accountRepository = Mock()
    ListAccounts listAccounts = new ListAccounts(accountRepository)

    def "should return a page of account data"() {
        given:
        def pageRequest = PageRequest.of(0, 10)
        def accounts = generateAccounts()

        when:
        Page<AccountData> accountData = listAccounts.apply(pageRequest)

        then:
        1 * accountRepository.findAll(pageRequest) >> new PageImpl<>(accounts)
        accountData.size() == accounts.size()
    }

    def generateAccounts() {
        (1..10).collect() {
            Account account = Mock()
            account.client >> Mock(Client)
            account
        }
    }

}
