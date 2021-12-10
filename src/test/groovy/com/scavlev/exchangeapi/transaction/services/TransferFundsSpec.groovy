package com.scavlev.exchangeapi.transaction.services

import com.scavlev.exchangeapi.account.domain.Account
import com.scavlev.exchangeapi.account.domain.AccountEntryType
import com.scavlev.exchangeapi.account.domain.AccountRepository
import com.scavlev.exchangeapi.transaction.OperationOnNonExistentAccountException
import com.scavlev.exchangeapi.transaction.ProcessTransactionRequest
import com.scavlev.exchangeapi.transaction.domain.Transaction
import com.scavlev.exchangeapi.transaction.domain.TransactionRepository
import com.scavlev.exchangeapi.transaction.domain.TransactionType
import spock.lang.Specification

class TransferFundsSpec extends Specification {

    TransactionRepository transactionRepository = Mock()
    AccountRepository accountRepository = Mock()
    TransferFunds transferFunds = new TransferFunds(transactionRepository, accountRepository)

    def "should throw exception if debit account is not found"() {
        given:
        def fromAccountId = 1
        def toAccountId = 2
        def amount = 10.11
        accountRepository.findById(fromAccountId) >> Optional.empty()
        accountRepository.findById(toAccountId) >> Optional.of(new Account())
        ProcessTransactionRequest request = new ProcessTransactionRequest(fromAccountId, toAccountId, amount)

        when:
        transferFunds.apply(request)

        then:
        thrown(OperationOnNonExistentAccountException)
    }

    def "should throw exception if credit account is not found"() {
        given:
        def fromAccountId = 1
        def toAccountId = 2
        def amount = 10.11
        accountRepository.findById(fromAccountId) >> Optional.of(new Account())
        accountRepository.findById(toAccountId) >> Optional.empty()
        ProcessTransactionRequest request = new ProcessTransactionRequest(fromAccountId, toAccountId, amount)

        when:
        transferFunds.apply(request)

        then:
        thrown(OperationOnNonExistentAccountException)
    }

    def "should correctly create and save transfer transaction"() {
        given:
        def fromAccountId = 1
        def fromAccountCurrency = "USD"
        def fromAccount = new Account(balance: 20, currency: fromAccountCurrency)
        def toAccountId = 2
        def toAccountCurrency = "USD"
        def toAccount = new Account(balance: 20, currency: toAccountCurrency)
        def amount = 10.0
        accountRepository.findById(fromAccountId) >> Optional.of(fromAccount)
        accountRepository.findById(toAccountId) >> Optional.of(toAccount)
        ProcessTransactionRequest request = new ProcessTransactionRequest(fromAccountId, toAccountId, amount)

        when:
        transferFunds.apply(request)

        then:
        1 * transactionRepository.saveAndFlush(_) >> { arguments ->
            def transaction = arguments[0]
            assert transaction instanceof Transaction
            assert transaction.transactionType == TransactionType.TRANSFER
            assert transaction.entries.size() == 2
            assert transaction.debitAccountEntry.present
            assert transaction.debitAccountEntry.get().amount == amount
            assert transaction.debitAccountEntry.get().type == AccountEntryType.DEBIT
            assert transaction.creditAccountEntry.present
            assert transaction.creditAccountEntry.get().amount == amount.negate()
            assert transaction.creditAccountEntry.get().type == AccountEntryType.CREDIT
            transaction
        }
        fromAccount.balance == 20 - amount
        toAccount.balance == 20 + amount
    }

}
