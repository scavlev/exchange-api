package com.scavlev.exchangeapi.transaction.services

import com.scavlev.exchangeapi.account.DeactivatedAccountAccessException
import com.scavlev.exchangeapi.account.domain.AccountRepository
import com.scavlev.exchangeapi.account.domain.AccountStatus
import com.scavlev.exchangeapi.currency.CurrencyExchangeService
import com.scavlev.exchangeapi.transaction.InsufficientFundsException
import com.scavlev.exchangeapi.transaction.OperationOnNonExistentAccountException
import com.scavlev.exchangeapi.transaction.ProcessTransactionRequest
import spock.lang.Specification

import static com.scavlev.exchangeapi.FixtureHelper.createAccount

class ProcessTransactionSpec extends Specification {

    AccountRepository accountRepository = Mock()
    CurrencyExchangeService currencyExchangeService = Mock()
    ExchangeFunds exchangeFunds = Mock()
    TransferFunds transferFunds = Mock()

    ProcessTransaction processTransaction = new ProcessTransaction(
            accountRepository,
            currencyExchangeService,
            exchangeFunds,
            transferFunds)

    def "should throw exception if debit account is not found"() {
        given:
        def fromAccountId = 1
        def toAccountId = 2
        def amount = 10.11
        accountRepository.findById(fromAccountId) >> Optional.empty()
        accountRepository.findById(toAccountId) >> Optional.of(createAccount())
        ProcessTransactionRequest request = new ProcessTransactionRequest(fromAccountId, toAccountId, amount)

        when:
        processTransaction.apply(request)

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
        processTransaction.apply(request)

        then:
        thrown(OperationOnNonExistentAccountException)
    }

    def "should throw exception if debit account is deactivated"() {
        given:
        def fromAccountId = 1
        def toAccountId = 2
        def amount = 10.11
        accountRepository.findById(fromAccountId) >> Optional.of(createAccount(status: AccountStatus.DEACTIVATED))
        accountRepository.findById(toAccountId) >> Optional.of(createAccount())
        ProcessTransactionRequest request = new ProcessTransactionRequest(fromAccountId, toAccountId, amount)

        when:
        processTransaction.apply(request)

        then:
        thrown(DeactivatedAccountAccessException)
    }

    def "should throw exception if credit account is deactivated"() {
        given:
        def fromAccountId = 1
        def toAccountId = 2
        def amount = 10.11
        accountRepository.findById(fromAccountId) >> Optional.of(createAccount())
        accountRepository.findById(toAccountId) >> Optional.of(createAccount(status: AccountStatus.DEACTIVATED))
        ProcessTransactionRequest request = new ProcessTransactionRequest(fromAccountId, toAccountId, amount)

        when:
        processTransaction.apply(request)

        then:
        thrown(DeactivatedAccountAccessException)
    }

    def "should throw exception if credit account has insufficient funds"() {
        given:
        def fromAccountId = 1
        def toAccountId = 2
        def amount = 10.11
        accountRepository.findById(fromAccountId) >> Optional.of(createAccount(balance: 5))
        accountRepository.findById(toAccountId) >> Optional.of(createAccount())
        ProcessTransactionRequest request = new ProcessTransactionRequest(fromAccountId, toAccountId, amount)

        when:
        processTransaction.apply(request)

        then:
        thrown(InsufficientFundsException)
    }

    def "should execute transfer if account currencies are the same"() {
        given:
        def fromAccountId = 1
        def toAccountId = 2
        def amount = 10.11
        accountRepository.findById(fromAccountId) >> Optional.of(createAccount(currency: "same", balance: 20))
        accountRepository.findById(toAccountId) >> Optional.of(createAccount(currency: "same"))
        ProcessTransactionRequest request = new ProcessTransactionRequest(fromAccountId, toAccountId, amount)

        when:
        processTransaction.apply(request)

        then:
        1 * transferFunds.apply(request)
        0 * exchangeFunds.apply(request, _)
    }

    def "should execute exchange if account currencies are different"() {
        given:
        def fromAccountId = 1
        def toAccountId = 2
        def amount = 10.11
        def fromAccount = createAccount(currency: "same", balance: 20)
        def toAccount = createAccount(currency: "different")
        def rate = 0.5
        accountRepository.findById(fromAccountId) >> Optional.of(fromAccount)
        accountRepository.findById(toAccountId) >> Optional.of(toAccount)
        ProcessTransactionRequest request = new ProcessTransactionRequest(fromAccountId, toAccountId, amount)

        when:
        processTransaction.apply(request)

        then:
        1 * currencyExchangeService.getRate(fromAccount.currency, toAccount.currency) >> rate
        1 * exchangeFunds.apply(request, rate)
        0 * transferFunds.apply(request)
    }
}
