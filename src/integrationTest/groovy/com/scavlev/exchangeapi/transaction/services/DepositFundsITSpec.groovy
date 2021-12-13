package com.scavlev.exchangeapi.transaction.services

import com.scavlev.exchangeapi.IntegrationSpecification
import com.scavlev.exchangeapi.account.domain.AccountEntryType
import com.scavlev.exchangeapi.transaction.DepositTransactionRequest
import com.scavlev.exchangeapi.transaction.TransactionData
import com.scavlev.exchangeapi.transaction.domain.Transaction
import com.scavlev.exchangeapi.transaction.domain.TransactionRepository
import com.scavlev.exchangeapi.transaction.domain.TransactionType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class DepositFundsITSpec extends IntegrationSpecification {

    @Autowired
    TransactionRepository transactionRepository

    @Autowired
    DepositFunds depositFunds

    @Transactional
    def "should deposit funds to account"() {
        given:
        long accountId = 22
        BigDecimal amount = 5

        when:
        TransactionData transactionData = depositFunds.deposit(new DepositTransactionRequest(accountId, amount))

        then:
        Transaction transaction = transactionRepository.getById(transactionData.id)
        with(transactionData) {
            id != null
            timestamp != null
            type == TransactionType.DEPOSIT
            !exchangeRate.present
            !fromAccount.present
            toAccount.present
            with(toAccount.get()) {
                id == accountId
                it.amount == amount
                currency == "GHS"
            }
        }
        with(transaction) {
            id == transactionData.id
            entityCreated == transactionData.timestamp
            transactionType == TransactionType.DEPOSIT
            !creditAccountEntry.present
            debitAccountEntry.present
            with(debitAccountEntry.get()) {
                it.account.id == accountId
                it.amount == amount
                type == AccountEntryType.DEBIT
            }
        }
    }

}
