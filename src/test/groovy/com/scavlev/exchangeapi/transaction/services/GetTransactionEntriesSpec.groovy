package com.scavlev.exchangeapi.transaction.services

import com.scavlev.exchangeapi.account.AccountEntryData
import com.scavlev.exchangeapi.account.domain.AccountEntryType
import com.scavlev.exchangeapi.transaction.TransactionEntryLoadException
import com.scavlev.exchangeapi.transaction.domain.Transaction
import com.scavlev.exchangeapi.transaction.domain.TransactionRepository
import com.scavlev.exchangeapi.transaction.domain.TransactionType
import spock.lang.Specification

import static com.scavlev.exchangeapi.FixtureHelper.createAccountEntry
import static com.scavlev.exchangeapi.FixtureHelper.createTransaction
import static com.scavlev.exchangeapi.account.AccountEntryData.fromAccountEntry

class GetTransactionEntriesSpec extends Specification {

    TransactionRepository transactionRepository = Mock()
    GetTransactionEntries getTransactionEntries = new GetTransactionEntries(transactionRepository)

    def "should fail if transaction not found"() {
        given:
        long transactionId = 1

        when:
        getTransactionEntries.get(transactionId)

        then:
        1 * transactionRepository.findById(transactionId) >> Optional.empty()
        thrown(TransactionEntryLoadException)
    }

    def "should return all transaction entry data"() {
        given:
        long transactionId = 1

        Transaction transaction = createTransaction(type: TransactionType.TRANSFER).with {
            entries.addAll(
                    createAccountEntry(type: AccountEntryType.DEBIT, transaction: it),
                    createAccountEntry(type: AccountEntryType.CREDIT, transaction: it)
            )
            it
        }

        when:
        List<AccountEntryData> accountEntryDataList = getTransactionEntries.get(transactionId)

        then:
        1 * transactionRepository.findById(transactionId) >> Optional.of(transaction)
        accountEntryDataList != null
        accountEntryDataList.size() == 2
        accountEntryDataList[0] == fromAccountEntry(transaction.entries[0])
        accountEntryDataList[1] == fromAccountEntry(transaction.entries[1])
    }

}
