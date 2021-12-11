package com.scavlev.exchangeapi.transaction.services

import com.scavlev.exchangeapi.account.DeactivatedAccountAccessException
import com.scavlev.exchangeapi.account.domain.AccountEntryType
import com.scavlev.exchangeapi.account.domain.AccountRepository
import com.scavlev.exchangeapi.account.domain.AccountStatus
import com.scavlev.exchangeapi.transaction.DepositTransactionRequest
import com.scavlev.exchangeapi.transaction.OperationOnNonExistentAccountException
import com.scavlev.exchangeapi.transaction.domain.Transaction
import com.scavlev.exchangeapi.transaction.domain.TransactionRepository
import com.scavlev.exchangeapi.transaction.domain.TransactionType
import spock.lang.Specification

import static com.scavlev.exchangeapi.FixtureHelper.createAccount

class DepositFundsSpec extends Specification {

    TransactionRepository transactionRepository = Mock()
    AccountRepository accountRepository = Mock()
    DepositFunds depositFunds = new DepositFunds(transactionRepository, accountRepository)

    def "should throw exception if no account is found"() {
        given:
        def accountId = 1
        def amount = 10.11
        accountRepository.findById(accountId) >> Optional.empty()
        DepositTransactionRequest request = new DepositTransactionRequest(accountId, amount)

        when:
        depositFunds.apply(request)

        then:
        thrown(OperationOnNonExistentAccountException)
    }

    def "should throw exception if account is deactivated"() {
        given:
        def accountId = 1
        def amount = 10.11
        accountRepository.findById(accountId) >> Optional.of(createAccount(status: AccountStatus.DEACTIVATED))
        DepositTransactionRequest request = new DepositTransactionRequest(accountId, amount)

        when:
        depositFunds.apply(request)

        then:
        thrown(DeactivatedAccountAccessException)
    }

    def "should create and save deposit transaction"() {
        given:
        def accountId = 1
        def amount = 10.11
        def account = createAccount(balance: 0.0)
        accountRepository.findById(accountId) >> Optional.of(account)
        DepositTransactionRequest request = new DepositTransactionRequest(accountId, amount)

        when:
        depositFunds.apply(request)

        then:
        1 * transactionRepository.saveAndFlush(_) >> { Transaction transaction ->
            assert transaction.transactionType == TransactionType.DEPOSIT
            assert transaction.entries.size() == 1
            assert transaction.debitAccountEntry.present
            assert transaction.debitAccountEntry.get().amount == amount
            assert transaction.debitAccountEntry.get().type == AccountEntryType.DEBIT
            transaction
        }
        account.balance == amount
    }

}