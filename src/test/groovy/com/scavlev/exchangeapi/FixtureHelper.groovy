package com.scavlev.exchangeapi

import com.scavlev.exchangeapi.account.domain.Account
import com.scavlev.exchangeapi.account.domain.AccountEntry
import com.scavlev.exchangeapi.account.domain.AccountEntryType
import com.scavlev.exchangeapi.account.domain.AccountStatus
import com.scavlev.exchangeapi.client.domain.Client
import com.scavlev.exchangeapi.client.domain.ClientStatus
import com.scavlev.exchangeapi.transaction.domain.ExchangeRate
import com.scavlev.exchangeapi.transaction.domain.Transaction
import com.scavlev.exchangeapi.transaction.domain.TransactionType
import org.springframework.test.util.ReflectionTestUtils

class FixtureHelper {

    static def createClient(Map params = [:]) {
        def defaultParams = [
                id    : 1,
                status: ClientStatus.ACTIVE
        ] << params
        Client client = Client.builder()
                .status(defaultParams.status as ClientStatus)
                .build()

        ReflectionTestUtils.setField(client, 'id', (defaultParams.id as Long))

        client
    }

    static def createAccount(Map params = [:], Closure<Account> c = null) {
        def defaultParams = [
                id      : 1,
                client  : createClient(),
                balance : 42,
                currency: "BTC",
                status  : AccountStatus.ACTIVE
        ] << params
        Account account = Account.builder()
                .client(defaultParams.client as Client)
                .balance(defaultParams.balance as BigDecimal)
                .currency(defaultParams.currency as String)
                .status(defaultParams.status as AccountStatus)
                .build()

        ReflectionTestUtils.setField(account, 'id', (defaultParams.id as Long))

        if (c != null) {
            c.delegate = account
            c()
        }

        account
    }

    static def createAccountEntry(Map params = [:]) {
        def defaultParams = [
                id         : 1,
                account    : createAccount(),
                transaction: createTransaction(),
                amount     : 42,
                type       : AccountEntryType.DEBIT
        ] << params
        AccountEntry accountEntry = AccountEntry.builder()
                .account(defaultParams.account as Account)
                .transaction(defaultParams.transaction as Transaction)
                .amount(defaultParams.amount as BigDecimal)
                .type(defaultParams.type as AccountEntryType)
                .build()

        ReflectionTestUtils.setField(accountEntry, 'id', (defaultParams.id as Long))

        accountEntry
    }

    static def createTransaction(Map params = [:]) {
        def defaultParams = [
                id             : 1,
                transactionType: TransactionType.DEPOSIT
        ] << params
        Transaction transaction = Transaction.builder()
                .transactionType(defaultParams.transactionType as TransactionType)
                .exchangeRate(defaultParams.exchangeRate as ExchangeRate)
                .build()

        ReflectionTestUtils.setField(transaction, 'id', (defaultParams.id as Long))

        transaction
    }

    static def createExchangeRate(Map params = [:]) {
        def defaultParams = [
                id          : 1,
                fromCurrency: "EUR",
                toCurrency  : "BTC",
                rate        : 0.42
        ] << params
        ExchangeRate exchangeRate = ExchangeRate.builder()
                .transaction(defaultParams.transaction as Transaction)
                .fromCurrency(defaultParams.fromCurrency as String)
                .toCurrency(defaultParams.toCurrency as String)
                .rate(defaultParams.rate as BigDecimal)
                .build()

        ReflectionTestUtils.setField(exchangeRate, 'id', (defaultParams.id as Long))

        exchangeRate
    }

}
