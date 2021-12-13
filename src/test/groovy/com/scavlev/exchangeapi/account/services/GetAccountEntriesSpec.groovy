package com.scavlev.exchangeapi.account.services

import com.scavlev.exchangeapi.account.AccountEntriesRetrievalException
import com.scavlev.exchangeapi.account.AccountEntryData
import com.scavlev.exchangeapi.account.domain.Account
import com.scavlev.exchangeapi.account.domain.AccountEntryRepository
import com.scavlev.exchangeapi.account.domain.AccountRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import spock.lang.Specification

import static com.scavlev.exchangeapi.FixtureHelper.createAccount
import static com.scavlev.exchangeapi.FixtureHelper.createAccountEntry
import static com.scavlev.exchangeapi.account.AccountEntryData.fromAccountEntry

class GetAccountEntriesSpec extends Specification {

    AccountEntryRepository accountEntryRepository = Mock()
    AccountRepository accountRepository = Mock()
    GetAccountEntries getAccountEntries = new GetAccountEntries(accountEntryRepository, accountRepository)

    def "should fail if account is not found"() {
        given:
        long accountId = 1
        PageRequest pageRequest = PageRequest.of(0, 10)

        when:
        getAccountEntries.get(accountId, pageRequest)

        then:
        1 * accountRepository.existsById(accountId) >> false
        thrown(AccountEntriesRetrievalException)
    }

    def "should return all account entry data"() {
        given:
        long accountId = 1
        PageRequest pageRequest = PageRequest.of(0, 10)

        Account account = createAccount().with {
            entries.addAll(
                    createAccountEntry(id: 1, account: it),
                    createAccountEntry(id: 2, account: it)
            )
            it
        }

        when:
        Page<AccountEntryData> accountEntryDataList = getAccountEntries.get(accountId, pageRequest)

        then:
        1 * accountRepository.existsById(accountId) >> true
        1 * accountEntryRepository.findByAccountId(accountId, pageRequest) >> new PageImpl<>(account.entries)
        accountEntryDataList != null
        accountEntryDataList.size() == 2
        accountEntryDataList[0] == fromAccountEntry(account.entries[0])
        accountEntryDataList[1] == fromAccountEntry(account.entries[1])
    }

}
