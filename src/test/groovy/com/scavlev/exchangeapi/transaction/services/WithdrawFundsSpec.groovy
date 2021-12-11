package com.scavlev.exchangeapi.transaction.services

import com.scavlev.exchangeapi.account.DeactivatedAccountAccessException
import com.scavlev.exchangeapi.account.domain.AccountEntryType
import com.scavlev.exchangeapi.account.domain.AccountRepository
import com.scavlev.exchangeapi.account.domain.AccountStatus
import com.scavlev.exchangeapi.transaction.InsufficientFundsException
import com.scavlev.exchangeapi.transaction.OperationOnNonExistentAccountException
import com.scavlev.exchangeapi.transaction.WithdrawalTransactionRequest
import com.scavlev.exchangeapi.transaction.domain.Transaction
import com.scavlev.exchangeapi.transaction.domain.TransactionRepository
import com.scavlev.exchangeapi.transaction.domain.TransactionType
import spock.lang.Specification

import static com.scavlev.exchangeapi.FixtureHelper.createAccount

class WithdrawFundsSpec extends Specification {

    TransactionRepository transactionRepository = Mock()
    AccountRepository accountRepository = Mock()
    WithdrawFunds withdrawFunds = new WithdrawFunds(transactionRepository, accountRepository)

    def "should throw exception if no account is found"() {
        given:
        def accountId = 1
        def amount = 10.11
        accountRepository.findById(accountId) >> Optional.empty()
        WithdrawalTransactionRequest request = new WithdrawalTransactionRequest(accountId, amount)

        when:
        withdrawFunds.apply(request)

        then:
        thrown(OperationOnNonExistentAccountException)
    }

    def "should throw exception if account is deactivated"() {
        given:
        def accountId = 1
        def amount = 10.11
        accountRepository.findById(accountId) >> Optional.of(createAccount(status: AccountStatus.DEACTIVATED))
        WithdrawalTransactionRequest request = new WithdrawalTransactionRequest(accountId, amount)

        when:
        withdrawFunds.apply(request)

        then:
        thrown(DeactivatedAccountAccessException)
    }

    def "should throw exception if account has insufficient funds"() {
        given:
        def accountId = 1
        def amount = 10.11
        accountRepository.findById(accountId) >> Optional.of(createAccount(balance: 10.0))
        WithdrawalTransactionRequest request = new WithdrawalTransactionRequest(accountId, amount)

        when:
        withdrawFunds.apply(request)

        then:
        thrown(InsufficientFundsException)
    }

    def "should create and save withdraw transaction"() {
        given:
        def accountId = 1
        def amount = 10.11
        def account = createAccount(balance: 20.0)
        accountRepository.findById(accountId) >> Optional.of(account)
        WithdrawalTransactionRequest request = new WithdrawalTransactionRequest(accountId, amount)

        when:
        withdrawFunds.apply(request)

        then:
        1 * transactionRepository.saveAndFlush(_) >> { Transaction transaction ->
            assert transaction.transactionType == TransactionType.WITHDRAWAL
            assert transaction.entries.size() == 1
            assert transaction.creditAccountEntry.present
            assert transaction.creditAccountEntry.get().account == account
            assert transaction.creditAccountEntry.get().amount == amount.negate()
            assert transaction.creditAccountEntry.get().type == AccountEntryType.CREDIT
            transaction
        }
        account.balance == 9.89
    }
}