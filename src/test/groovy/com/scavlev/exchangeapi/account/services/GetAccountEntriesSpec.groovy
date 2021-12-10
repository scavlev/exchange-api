package com.scavlev.exchangeapi.account.services

import com.scavlev.exchangeapi.account.AccountEntriesRetrievalException
import com.scavlev.exchangeapi.account.AccountEntryData
import com.scavlev.exchangeapi.account.domain.*
import com.scavlev.exchangeapi.transaction.domain.Transaction
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import spock.lang.Specification

class GetAccountEntriesSpec extends Specification {

    AccountEntryRepository accountEntryRepository = Mock()
    AccountRepository accountRepository = Mock()
    GetAccountEntries getAccountEntries = new GetAccountEntries(accountEntryRepository, accountRepository)

    def "should fail if account is not found"() {
        given:
        def accountId = 1
        def pageRequest = PageRequest.of(0, 10)

        when:
        getAccountEntries.apply(accountId, pageRequest)

        then:
        1 * accountRepository.existsById(accountId) >> false
        thrown(AccountEntriesRetrievalException)
    }

    def "should return all account entry data"() {
        given:
        def accountId = 1
        def pageRequest = PageRequest.of(0, 10)

        Transaction transaction = Mock()
        transaction.id >> 1

        Account account = Mock()
        account.id >> accountId
        account.currency >> "EUR"

        AccountEntry debitEntry = Mock()
        debitEntry.transaction >> transaction
        debitEntry.amount >> 10
        debitEntry.type >> AccountEntryType.DEBIT
        debitEntry.account >> account

        AccountEntry creditEntry = Mock()
        creditEntry.transaction >> transaction
        creditEntry.amount >> 20
        creditEntry.type >> AccountEntryType.CREDIT
        creditEntry.account >> account

        when:
        Page<AccountEntryData> accountEntryDataList = getAccountEntries.apply(accountId, pageRequest)

        then:
        1 * accountRepository.existsById(accountId) >> true
        1 * accountEntryRepository.findByAccountId(accountId, pageRequest) >> new PageImpl<>([debitEntry, creditEntry])
        accountEntryDataList != null
        accountEntryDataList.size() == 2
        accountEntryDataList[0].amount == debitEntry.amount
        accountEntryDataList[0].currency == account.currency
        accountEntryDataList[0].type == debitEntry.type
        accountEntryDataList[0].accountId == account.id
        accountEntryDataList[0].transactionId == transaction.id
        accountEntryDataList[1].amount == creditEntry.amount
        accountEntryDataList[1].currency == account.currency
        accountEntryDataList[1].type == creditEntry.type
        accountEntryDataList[1].accountId == account.id
        accountEntryDataList[1].transactionId == transaction.id
    }
}
