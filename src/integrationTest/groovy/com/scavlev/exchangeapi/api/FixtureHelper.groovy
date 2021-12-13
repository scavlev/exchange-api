package com.scavlev.exchangeapi.api

import com.scavlev.exchangeapi.account.AccountData
import com.scavlev.exchangeapi.account.domain.AccountStatus
import com.scavlev.exchangeapi.client.ClientData
import com.scavlev.exchangeapi.client.domain.ClientStatus
import com.scavlev.exchangeapi.transaction.TransactionData
import com.scavlev.exchangeapi.transaction.domain.TransactionType

import java.time.ZonedDateTime

class FixtureHelper {

    static AccountData accountDataFixture(Map params = [:]) {
        def defaultParams = [id      : 1,
                             clientId: (params.clientId ?: (params.id ?: 1) * 77),
                             currency: "EUR",
                             status  : AccountStatus.ACTIVE,
                             balance : (params.balance ?: (params.id ?: 1) * 16.5)] << params
        AccountData.builder()
                .clientId(defaultParams.clientId as Long)
                .id(defaultParams.id as Long)
                .currency(defaultParams.currency as String)
                .status(defaultParams.status as AccountStatus)
                .balance(defaultParams.balance as BigDecimal)
                .build()
    }

    static ClientData clientDataFixture(Map params = [:]) {
        def defaultParams = [id    : 1,
                             status: ClientStatus.ACTIVE] << params
        ClientData.builder()
                .id(defaultParams.id as Long)
                .status(defaultParams.status as ClientStatus)
                .build()
    }

    static TransactionData exchangeTransactionDataFixture(Map params = [:]) {
        def defaultParams = [id         : 1,
                             fromAccount: (params.fromAccount ?: (params.id ?: 1) * 25),
                             toAccount  : (params.toAccount ?: (params.id ?: 1) * 12),
                             amount     : (params.amount ?: (params.id ?: 1) * 15),
                             rate       : (params.rate ?: (params.id ?: 1) * 0.7)] << params
        TransactionData.builder()
                .id(defaultParams.id as Long)
                .timestamp(ZonedDateTime.parse('2021-12-10T20:17:22.476322+00'))
                .type(TransactionType.EXCHANGE)
                .exchangeRate(TransactionData.Rate.builder()
                        .rate(defaultParams.rate as BigDecimal)
                        .fromCurrency("EUR")
                        .toCurrency("USD")
                        .build())
                .toAccount(TransactionData.ToAccount.builder()
                        .id(defaultParams.toAccount as Long)
                        .currency("EUR")
                        .amount((defaultParams.amount as BigDecimal) * (defaultParams.rate as BigDecimal))
                        .build())
                .fromAccount(TransactionData.FromAccount.builder()
                        .id(defaultParams.fromAccount as Long)
                        .currency("USD")
                        .amount((defaultParams.amount as BigDecimal).negate())
                        .build())
                .build()
    }

    static TransactionData transferTransactionDataFixture(Map params = [:]) {
        def defaultParams = [id         : 1,
                             fromAccount: (params.fromAccount ?: (params.id ?: 1) * 25),
                             toAccount  : (params.toAccount ?: (params.id ?: 1) * 12),
                             amount     : (params.amount ?: (params.id ?: 1) * 15)] << params
        TransactionData.builder()
                .id(defaultParams.id as Long)
                .timestamp(ZonedDateTime.parse('2021-12-10T20:17:22.476322+00'))
                .type(TransactionType.TRANSFER)
                .toAccount(TransactionData.ToAccount.builder()
                        .id(defaultParams.toAccount as Long)
                        .currency("EUR")
                        .amount((defaultParams.amount as BigDecimal))
                        .build())
                .fromAccount(TransactionData.FromAccount.builder()
                        .id(defaultParams.fromAccount as Long)
                        .currency("EUR")
                        .amount((defaultParams.amount as BigDecimal).negate())
                        .build())
                .build()
    }

    static TransactionData depositTransactionDataFixture(Map params = [:]) {
        def defaultParams = [id       : 1,
                             toAccount: (params.toAccount ?: (params.id ?: 1) * 12),
                             amount   : (params.amount ?: (params.id ?: 1) * 15)] << params
        TransactionData.builder()
                .id(defaultParams.id as Long)
                .timestamp(ZonedDateTime.parse('2021-12-10T20:17:22.476322+00'))
                .type(TransactionType.DEPOSIT)
                .toAccount(TransactionData.ToAccount.builder()
                        .id(defaultParams.toAccount as Long)
                        .currency("EUR")
                        .amount((defaultParams.amount as BigDecimal))
                        .build())
                .build()
    }

    static TransactionData withdrawalTransactionDataFixture(Map params = [:]) {
        def defaultParams = [id         : 1,
                             fromAccount: (params.fromAccount ?: (params.id ?: 1) * 25),
                             amount     : (params.amount ?: (params.id ?: 1) * 15)] << params
        TransactionData.builder()
                .id(defaultParams.id as Long)
                .timestamp(ZonedDateTime.parse('2021-12-10T20:17:22.476322+00'))
                .type(TransactionType.WITHDRAWAL)
                .fromAccount(TransactionData.FromAccount.builder()
                        .id(defaultParams.fromAccount as Long)
                        .currency("USD")
                        .amount((defaultParams.amount as BigDecimal).negate())
                        .build())
                .build()
    }
}
