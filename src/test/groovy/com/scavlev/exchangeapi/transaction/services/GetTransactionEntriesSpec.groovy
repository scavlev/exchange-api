package com.scavlev.exchangeapi.transaction.services

import com.scavlev.exchangeapi.account.AccountEntryData
import com.scavlev.exchangeapi.account.domain.Account
import com.scavlev.exchangeapi.account.domain.AccountEntry
import com.scavlev.exchangeapi.account.domain.AccountEntryType
import com.scavlev.exchangeapi.transaction.TransactionEntryLoadException
import com.scavlev.exchangeapi.transaction.domain.Transaction
import com.scavlev.exchangeapi.transaction.domain.TransactionRepository
import spock.lang.Specification

class GetTransactionEntriesSpec extends Specification {

    TransactionRepository transactionRepository = Mock()
    GetTransactionEntries getTransactionEntries = new GetTransactionEntries(transactionRepository)

    def "should fail if transaction not found"() {
        given:
        def transactionId = 1

        when:
        getTransactionEntries.apply(transactionId)

        then:
        1 * transactionRepository.findById(transactionId) >> Optional.empty()
        thrown(TransactionEntryLoadException)
    }

    def "should return all transaction entry data"() {
        given:
        def transactionId = 1

        Transaction transaction = Mock()
        transaction.id >> transactionId

        Account debitAccount = Mock()
        debitAccount.id >> 1
        debitAccount.currency >> "USD"

        AccountEntry debitEntry = Mock()
        debitEntry.transaction >> transaction
        debitEntry.amount >> 10
        debitEntry.type >> AccountEntryType.DEBIT
        debitEntry.account >> debitAccount

        Account creditAccount = Mock()
        creditAccount.id >> 2
        debitAccount.currency >> "EUR"

        AccountEntry creditEntry = Mock()
        creditEntry.transaction >> transaction
        creditEntry.amount >> 20
        creditEntry.type >> AccountEntryType.CREDIT
        creditEntry.account >> creditAccount

        transaction.entries >> [debitEntry, creditEntry]

        when:
        List<AccountEntryData> accountEntryDataList = getTransactionEntries.apply(transactionId)

        then:
        1 * transactionRepository.findById(transactionId) >> Optional.of(transaction)
        accountEntryDataList != null
        accountEntryDataList.size() == 2
        accountEntryDataList[0].amount == debitEntry.amount
        accountEntryDataList[0].currency == debitAccount.currency
        accountEntryDataList[0].type == debitEntry.type
        accountEntryDataList[0].accountId == debitAccount.id
        accountEntryDataList[0].transactionId == transaction.id
        accountEntryDataList[1].amount == creditEntry.amount
        accountEntryDataList[1].currency == creditAccount.currency
        accountEntryDataList[1].type == creditEntry.type
        accountEntryDataList[1].accountId == creditAccount.id
        accountEntryDataList[1].transactionId == transaction.id
    }
}
