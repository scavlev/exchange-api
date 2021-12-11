package com.scavlev.exchangeapi.api

import com.scavlev.exchangeapi.account.AccountEntryData
import com.scavlev.exchangeapi.account.DeactivatedAccountAccessException
import com.scavlev.exchangeapi.account.domain.AccountEntryType
import com.scavlev.exchangeapi.currency.CurrencyExchangeRatesUnavailableException
import com.scavlev.exchangeapi.currency.CurrencyPairNotFoundException
import com.scavlev.exchangeapi.transaction.*
import com.scavlev.exchangeapi.transaction.services.*
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import java.time.LocalDateTime

import static com.scavlev.exchangeapi.api.FixtureHelper.*
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(TransactionController)
@AutoConfigureMockMvc
@ContextConfiguration(classes = [DataModelAssemblerConfiguration])
class TransactionControllerSpec extends Specification {

    @Autowired
    MockMvc mvc

    @SpringBean
    ProcessTransaction processTransaction = Mock()
    @SpringBean
    DepositFunds depositFunds = Mock()
    @SpringBean
    WithdrawFunds withdrawFunds = Mock()
    @SpringBean
    ListTransactions listTransactions = Mock()
    @SpringBean
    FindTransaction findTransaction = Mock()
    @SpringBean
    GetTransactionEntries getTransactionEntries = Mock()

    def "should return list of transactions"() {
        given:
        1 * listTransactions.apply(_ as Pageable) >> { PageRequest pageRequest ->
            new PageImpl<TransactionData>((1..50).collect({
                exchangeTransactionDataFixture(id: it)
            }), pageRequest, 50)
        }

        expect:
        mvc.perform(get("/transactions?page=1&size=10"))
                .andExpect(status().isOk())
                .andExpect(content().json(getResponseExample('list')))
                .andReturn()
    }

    def "should return transaction"() {
        given:
        1 * findTransaction.apply(_) >> { Long id ->
            Optional.of(exchangeTransactionDataFixture(id: id))
        }

        expect:
        mvc.perform(get("/transactions/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(getResponseExample('get-transaction')))
                .andReturn()
    }

    def "should return status code 404 if transaction is not found"() {
        given:
        1 * findTransaction.apply(_) >> { Long id ->
            throw new TransactionNotFoundException(id)
        }

        expect:
        mvc.perform(get("/transactions/1"))
                .andExpect(status().isNotFound())
                .andReturn()
    }

    def "should return a list of transaction entries"() {
        given:
        1 * getTransactionEntries.apply(_ as Long) >> { Long id ->
            (1..5).collect({
                AccountEntryData.builder()
                        .accountId(id)
                        .transactionId(it * 16)
                        .currency("EUR")
                        .timestamp(LocalDateTime.parse('2021-12-10T20:17:22.476322'))
                        .type(AccountEntryType.DEBIT)
                        .amount(it * 77.3)
                        .build()
            })
        }

        expect:
        mvc.perform(get("/transactions/1/entries"))
                .andExpect(status().isOk())
                .andExpect(content().json(getResponseExample('entries')))
                .andReturn()
    }

    def "should return status 400 if transaction is not found while listing entries"() {
        given:
        1 * getTransactionEntries.apply(_ as Long) >> { Long id ->
            throw new TransactionEntryLoadException(id)
        }

        expect:
        mvc.perform(get("/transactions/1/entries"))
                .andExpect(status().isBadRequest())
                .andReturn()
    }

    def "should process exchange transaction"() {
        given:
        1 * processTransaction.apply(_ as ProcessTransactionRequest) >> { ProcessTransactionRequest processTransactionRequest ->
            exchangeTransactionDataFixture(
                    fromAccount: processTransactionRequest.fromAccount,
                    toAccount: processTransactionRequest.toAccount,
                    amount: processTransactionRequest.amount,
                    rate: 0.8)
        }

        expect:
        mvc.perform(post("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content('{ "fromAccount": 1, "toAccount": 2, "amount": 10 }'))
                .andExpect(status().isOk())
                .andExpect(content().json(getResponseExample('exchange')))
                .andReturn()
    }

    def "should return status code 409 if target account receivables are less than 0.01"() {
        given:
        1 * processTransaction.apply(_ as ProcessTransactionRequest) >> { ProcessTransactionRequest processTransactionRequest ->
            throw new InvalidReceivableAmount("")
        }

        expect:
        mvc.perform(post("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content('{ "fromAccount": 1, "toAccount": 2, "amount": 10 }'))
                .andExpect(status().isConflict())
                .andReturn()
    }

    def "should return status code 409 if exchange currency paid is not found"() {
        given:
        1 * processTransaction.apply(_ as ProcessTransactionRequest) >> { ProcessTransactionRequest processTransactionRequest ->
            throw new CurrencyPairNotFoundException("", "")
        }

        expect:
        mvc.perform(post("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content('{ "fromAccount": 1, "toAccount": 2, "amount": 10 }'))
                .andExpect(status().isConflict())
                .andReturn()
    }

    def "should return status code 503 exchange service is unavailable"() {
        given:
        1 * processTransaction.apply(_ as ProcessTransactionRequest) >> { ProcessTransactionRequest processTransactionRequest ->
            throw new CurrencyExchangeRatesUnavailableException("", "", null)
        }

        expect:
        mvc.perform(post("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content('{ "fromAccount": 1, "toAccount": 2, "amount": 10 }'))
                .andExpect(status().isServiceUnavailable())
                .andReturn()
    }

    def "should process transfer transaction"() {
        given:
        1 * processTransaction.apply(_ as ProcessTransactionRequest) >> { ProcessTransactionRequest processTransactionRequest ->
            transferTransactionDataFixture(
                    fromAccount: processTransactionRequest.fromAccount,
                    toAccount: processTransactionRequest.toAccount,
                    amount: processTransactionRequest.amount)
        }

        expect:
        mvc.perform(post("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content('{ "fromAccount": 1, "toAccount": 2, "amount": 10 }'))
                .andExpect(status().isOk())
                .andExpect(content().json(getResponseExample('transfer')))
                .andReturn()
    }

    def "should return status code 400 if transferring between same account"() {
        given:
        0 * processTransaction.apply(_ as ProcessTransactionRequest)

        expect:
        mvc.perform(post("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content('{ "fromAccount": 1, "toAccount": 1, "amount": 10 }'))
                .andExpect(status().isBadRequest())
                .andReturn()
    }

    def "should return status code 400 if base account has insufficient funds for transaction"() {
        given:
        1 * processTransaction.apply(_ as ProcessTransactionRequest) >> { ProcessTransactionRequest processTransactionRequest ->
            throw new InsufficientFundsException(processTransactionRequest.fromAccount)
        }

        expect:
        mvc.perform(post("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content('{ "fromAccount": 1, "toAccount": 2, "amount": 10 }'))
                .andExpect(status().isBadRequest())
                .andReturn()
    }

    def "should return status code 400 if any account is deactivated"() {
        given:
        1 * processTransaction.apply(_ as ProcessTransactionRequest) >> { ProcessTransactionRequest processTransactionRequest ->
            throw new DeactivatedAccountAccessException("")
        }

        expect:
        mvc.perform(post("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content('{ "fromAccount": 1, "toAccount": 2, "amount": 10 }'))
                .andExpect(status().isBadRequest())
                .andReturn()
    }

    def "should process deposit transaction"() {
        given:
        1 * depositFunds.apply(_ as DepositTransactionRequest) >> { DepositTransactionRequest depositTransactionRequest ->
            depositTransactionDataFixture(
                    toAccount: depositTransactionRequest.toAccount,
                    amount: depositTransactionRequest.amount)
        }

        expect:
        mvc.perform(post("/transactions/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .content('{ "toAccount": 1, "amount": 10 }'))
                .andExpect(status().isOk())
                .andExpect(content().json(getResponseExample('deposit')))
                .andReturn()
    }

    def "should return status code 400 if deposit account is deactivated"() {
        given:
        1 * depositFunds.apply(_ as DepositTransactionRequest) >> { DepositTransactionRequest depositTransactionRequest ->
            throw new DeactivatedAccountAccessException("")
        }

        expect:
        mvc.perform(post("/transactions/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .content('{ "toAccount": 1, "amount": 10 }'))
                .andExpect(status().isBadRequest())
                .andReturn()
    }

    def "should return status code 400 if there is no account to deposit to"() {
        given:
        1 * depositFunds.apply(_ as DepositTransactionRequest) >> { DepositTransactionRequest depositTransactionRequest ->
            throw new OperationOnNonExistentAccountException(depositTransactionRequest.getToAccount())
        }

        expect:
        mvc.perform(post("/transactions/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .content('{ "toAccount": 1, "amount": 10 }'))
                .andExpect(status().isBadRequest())
                .andReturn()
    }

    def "should process withdrawal transaction"() {
        given:
        1 * withdrawFunds.apply(_ as WithdrawalTransactionRequest) >> { WithdrawalTransactionRequest withdrawalTransactionRequest ->
            withdrawalTransactionDataFixture(
                    fromAccount: withdrawalTransactionRequest.fromAccount,
                    amount: withdrawalTransactionRequest.amount)
        }

        expect:
        mvc.perform(post("/transactions/withdrawal")
                .contentType(MediaType.APPLICATION_JSON)
                .content('{ "fromAccount": 1, "amount": 10 }'))
                .andExpect(status().isOk())
                .andExpect(content().json(getResponseExample('withdrawal')))
                .andReturn()
    }

    def "should return status code 400 if there is insufficient funds during withdrawal"() {
        given:
        1 * withdrawFunds.apply(_ as WithdrawalTransactionRequest) >> { WithdrawalTransactionRequest withdrawalTransactionRequest ->
            throw new InsufficientFundsException(withdrawalTransactionRequest.fromAccount)
        }

        expect:
        mvc.perform(post("/transactions/withdrawal")
                .contentType(MediaType.APPLICATION_JSON)
                .content('{ "fromAccount": 1, "amount": 10 }'))
                .andExpect(status().isBadRequest())
                .andReturn()
    }

    def "should return status code 400 if withdrawal account is deactivated"() {
        given:
        1 * withdrawFunds.apply(_ as WithdrawalTransactionRequest) >> { WithdrawalTransactionRequest withdrawalTransactionRequest ->
            throw new DeactivatedAccountAccessException("")
        }

        expect:
        mvc.perform(post("/transactions/withdrawal")
                .contentType(MediaType.APPLICATION_JSON)
                .content('{ "fromAccount": 1, "amount": 10 }'))
                .andExpect(status().isBadRequest())
                .andReturn()
    }

    def "should return status code 400 if there is no account to withdraw from"() {
        given:
        1 * withdrawFunds.apply(_ as WithdrawalTransactionRequest) >> { WithdrawalTransactionRequest withdrawalTransactionRequest ->
            throw new OperationOnNonExistentAccountException(withdrawalTransactionRequest.fromAccount)
        }

        expect:
        mvc.perform(post("/transactions/withdrawal")
                .contentType(MediaType.APPLICATION_JSON)
                .content('{ "fromAccount": 1, "amount": 10 }'))
                .andExpect(status().isBadRequest())
                .andReturn()
    }

    static def getResponseExample(String filename) {
        new File("src/integrationTest/resources/api-responses/transactions/${filename}.json").text
    }
}
