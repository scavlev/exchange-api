package com.scavlev.exchangeapi.transaction.services

import com.scavlev.exchangeapi.account.domain.Account
import com.scavlev.exchangeapi.account.domain.AccountEntryType
import com.scavlev.exchangeapi.account.domain.AccountRepository
import com.scavlev.exchangeapi.transaction.InvalidReceivableAmount
import com.scavlev.exchangeapi.transaction.OperationOnNonExistentAccountException
import com.scavlev.exchangeapi.transaction.ProcessTransactionRequest
import com.scavlev.exchangeapi.transaction.domain.Transaction
import com.scavlev.exchangeapi.transaction.domain.TransactionRepository
import com.scavlev.exchangeapi.transaction.domain.TransactionType
import spock.lang.Specification

import static com.scavlev.exchangeapi.FixtureHelper.createAccount

class ExchangeFundsSpec extends Specification {

    TransactionRepository transactionRepository = Mock()
    AccountRepository accountRepository = Mock()
    ExchangeFunds exchangeFunds = new ExchangeFunds(transactionRepository, accountRepository)

    def "should throw exception if debit account is not found"() {
        given:
        long fromAccountId = 1
        long toAccountId = 2
        BigDecimal amount = 10.11
        BigDecimal rate = 0.5
        accountRepository.findById(fromAccountId) >> Optional.empty()
        accountRepository.findById(toAccountId) >> Optional.of(createAccount(id: toAccountId))
        ProcessTransactionRequest request = new ProcessTransactionRequest(fromAccountId, toAccountId, amount)

        when:
        exchangeFunds.exchange(request, rate)

        then:
        thrown(OperationOnNonExistentAccountException)
    }

    def "should throw exception if credit account is not found"() {
        given:
        long fromAccountId = 1
        long toAccountId = 2
        BigDecimal amount = 10.11
        BigDecimal rate = 0.5
        accountRepository.findById(fromAccountId) >> Optional.of(createAccount(id: fromAccountId))
        accountRepository.findById(toAccountId) >> Optional.empty()
        ProcessTransactionRequest request = new ProcessTransactionRequest(fromAccountId, toAccountId, amount)

        when:
        exchangeFunds.exchange(request, rate)

        then:
        thrown(OperationOnNonExistentAccountException)
    }

    def "should throw exception if debit account receivables are less than 0.01"() {
        given:
        long fromAccountId = 1
        long toAccountId = 2
        BigDecimal amount = 0.01
        BigDecimal rate = 0.5
        accountRepository.findById(fromAccountId) >> Optional.of(createAccount(id: fromAccountId, balance: 0))
        accountRepository.findById(toAccountId) >> Optional.of(createAccount(id: toAccountId, balance: 0))
        ProcessTransactionRequest request = new ProcessTransactionRequest(fromAccountId, toAccountId, amount)

        when:
        exchangeFunds.exchange(request, rate)

        then:
        thrown(InvalidReceivableAmount)
    }

    def "should correctly create and save exchange transaction"() {
        given:
        long fromAccountId = 1
        Account fromAccount = createAccount(id: fromAccountId, balance: 20, currency: "USD")
        long toAccountId = 2
        Account toAccount = createAccount(id: toAccountId, balance: 20, currency: "EUR")
        BigDecimal amount = 10.0
        BigDecimal rate = 0.5
        accountRepository.findById(fromAccountId) >> Optional.of(fromAccount)
        accountRepository.findById(toAccountId) >> Optional.of(toAccount)
        ProcessTransactionRequest request = new ProcessTransactionRequest(fromAccountId, toAccountId, amount)

        when:
        exchangeFunds.exchange(request, rate)

        then:
        1 * transactionRepository.saveAndFlush(_) >> { Transaction transaction ->
            assert transaction.transactionType == TransactionType.EXCHANGE
            assert transaction.entries.size() == 2
            assert transaction.debitAccountEntry.present
            assert transaction.debitAccountEntry.get().amount == amount * rate
            assert transaction.debitAccountEntry.get().type == AccountEntryType.DEBIT
            assert transaction.creditAccountEntry.present
            assert transaction.creditAccountEntry.get().amount == amount.negate()
            assert transaction.creditAccountEntry.get().type == AccountEntryType.CREDIT
            assert transaction.exchangeRate.present
            assert transaction.exchangeRate.get().fromCurrency == fromAccount.currency
            assert transaction.exchangeRate.get().toCurrency == toAccount.currency
            assert transaction.exchangeRate.get().rate == rate
            transaction
        }
        fromAccount.balance == 20 - amount
        toAccount.balance == 20 + amount * rate
    }

}
