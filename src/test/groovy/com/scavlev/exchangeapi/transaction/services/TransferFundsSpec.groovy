package com.scavlev.exchangeapi.transaction.services


import com.scavlev.exchangeapi.account.domain.AccountEntryType
import com.scavlev.exchangeapi.account.domain.AccountRepository
import com.scavlev.exchangeapi.transaction.OperationOnNonExistentAccountException
import com.scavlev.exchangeapi.transaction.ProcessTransactionRequest
import com.scavlev.exchangeapi.transaction.domain.Transaction
import com.scavlev.exchangeapi.transaction.domain.TransactionRepository
import com.scavlev.exchangeapi.transaction.domain.TransactionType
import spock.lang.Specification

import static com.scavlev.exchangeapi.FixtureHelper.createAccount

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
        accountRepository.findById(toAccountId) >> Optional.of(createAccount())
        ProcessTransactionRequest request = new ProcessTransactionRequest(fromAccountId, toAccountId, amount)

        when:
        transferFunds.transfer(request)

        then:
        thrown(OperationOnNonExistentAccountException)
    }

    def "should throw exception if credit account is not found"() {
        given:
        def fromAccountId = 1
        def toAccountId = 2
        def amount = 10.11
        accountRepository.findById(fromAccountId) >> Optional.of(createAccount())
        accountRepository.findById(toAccountId) >> Optional.empty()
        ProcessTransactionRequest request = new ProcessTransactionRequest(fromAccountId, toAccountId, amount)

        when:
        transferFunds.transfer(request)

        then:
        thrown(OperationOnNonExistentAccountException)
    }

    def "should correctly create and save transfer transaction"() {
        given:
        def fromAccountId = 1
        def fromAccount = createAccount(balance: 20, currency: "USD")
        def toAccountId = 2
        def toAccount = createAccount(balance: 20, currency: "USD")
        def amount = 10.0
        accountRepository.findById(fromAccountId) >> Optional.of(fromAccount)
        accountRepository.findById(toAccountId) >> Optional.of(toAccount)
        ProcessTransactionRequest request = new ProcessTransactionRequest(fromAccountId, toAccountId, amount)

        when:
        transferFunds.transfer(request)

        then:
        1 * transactionRepository.saveAndFlush(_) >> { Transaction transaction ->
            assert transaction.transactionType == TransactionType.TRANSFER
            assert transaction.entries.size() == 2
            assert transaction.debitAccountEntry.present
            assert transaction.debitAccountEntry.get().amount == amount
            assert transaction.debitAccountEntry.get().account == toAccount
            assert transaction.debitAccountEntry.get().type == AccountEntryType.DEBIT
            assert transaction.creditAccountEntry.present
            assert transaction.creditAccountEntry.get().amount == amount.negate()
            assert transaction.creditAccountEntry.get().account == fromAccount
            assert transaction.creditAccountEntry.get().type == AccountEntryType.CREDIT
            transaction
        }
        fromAccount.balance == 20 - amount
        toAccount.balance == 20 + amount
    }

}
