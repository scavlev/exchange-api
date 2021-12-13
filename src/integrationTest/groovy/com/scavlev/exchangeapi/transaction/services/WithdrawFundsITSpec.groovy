package com.scavlev.exchangeapi.transaction.services

import com.scavlev.exchangeapi.IntegrationSpecification
import com.scavlev.exchangeapi.account.domain.AccountEntryType
import com.scavlev.exchangeapi.transaction.TransactionData
import com.scavlev.exchangeapi.transaction.WithdrawalTransactionRequest
import com.scavlev.exchangeapi.transaction.domain.Transaction
import com.scavlev.exchangeapi.transaction.domain.TransactionRepository
import com.scavlev.exchangeapi.transaction.domain.TransactionType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class WithdrawFundsITSpec extends IntegrationSpecification {

    @Autowired
    TransactionRepository transactionRepository

    @Autowired
    WithdrawFunds withdrawFunds

    @Transactional
    def "should withdraw funds from account"() {
        given:
        long accountId = 22
        BigDecimal amount = 5

        when:
        TransactionData transactionData = withdrawFunds.withdraw(new WithdrawalTransactionRequest(accountId, amount))

        then:
        Transaction transaction = transactionRepository.getById(transactionData.id)
        with(transactionData) {
            id != null
            timestamp != null
            type == TransactionType.WITHDRAWAL
            !exchangeRate.present
            !toAccount.present
            fromAccount.present
            with(fromAccount.get()) {
                id == accountId
                it.amount == amount.negate()
                currency == "GHS"
            }
        }
        with(transaction) {
            id == transactionData.id
            entityCreated == transactionData.timestamp
            transactionType == TransactionType.WITHDRAWAL
            !debitAccountEntry.present
            creditAccountEntry.present
            with(creditAccountEntry.get()) {
                it.account.id == accountId
                it.amount == amount.negate()
                type == AccountEntryType.CREDIT
            }
        }
    }
}
