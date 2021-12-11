package com.scavlev.exchangeapi.account.services

import com.scavlev.exchangeapi.account.AccountData
import com.scavlev.exchangeapi.account.domain.AccountRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import spock.lang.Specification

import static com.scavlev.exchangeapi.FixtureHelper.createAccount

class ListAccountsSpec extends Specification {

    AccountRepository accountRepository = Mock()
    ListAccounts listAccounts = new ListAccounts(accountRepository)

    def "should return a page of account data"() {
        given:
        def pageRequest = PageRequest.of(0, 10)
        def accounts = (1..10).collect {
            createAccount(id: it)
        }

        when:
        Page<AccountData> accountData = listAccounts.apply(pageRequest)

        then:
        1 * accountRepository.findAll(pageRequest) >> new PageImpl<>(accounts)
        accountData.size() == accounts.size()
    }

}
