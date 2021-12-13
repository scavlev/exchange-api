package com.scavlev.exchangeapi.api

import com.scavlev.exchangeapi.account.*
import com.scavlev.exchangeapi.account.domain.AccountEntryType
import com.scavlev.exchangeapi.account.services.*
import com.scavlev.exchangeapi.client.ClientDoesntExistException
import com.scavlev.exchangeapi.currency.SupportedCurrencies
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

import java.time.OffsetDateTime

import static com.scavlev.exchangeapi.api.FixtureHelper.accountDataFixture
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(AccountController)
@AutoConfigureMockMvc
@ContextConfiguration(classes = [DataModelAssemblerConfiguration])
class AccountControllerITSpec extends Specification {

    @Autowired
    MockMvc mvc

    @SpringBean
    ListAccounts listAccounts = Mock()
    @SpringBean
    FindAccount findAccount = Mock()
    @SpringBean
    DeactivateAccount deactivateAccount = Mock()
    @SpringBean
    OpenAccount openAccount = Mock()
    @SpringBean
    GetAccountEntries getAccountEntries = Mock()
    @SpringBean
    UpdateAccount updateAccount = Mock()
    @SpringBean
    SupportedCurrencies supportedCurrencies = Mock()

    def "should return list of accounts"() {
        given:
        1 * listAccounts.list(_ as Pageable) >> { PageRequest pageRequest ->
            new PageImpl<AccountData>((1..50).collect({
                accountDataFixture(id: it)
            }), pageRequest, 50)
        }

        expect:
        mvc.perform(get("/accounts?page=1&size=10"))
                .andExpect(status().isOk())
                .andExpect(content().json(getResponseExample('list')))
                .andReturn()
    }

    def "should return an account"() {
        given:
        1 * findAccount.find(_) >> { Long id ->
            Optional.of(accountDataFixture(id: id))
        }

        expect:
        mvc.perform(get("/accounts/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(getResponseExample('get-account')))
                .andReturn()
    }

    def "should return status 404 if account is not found"() {
        given:
        int accountId = 1
        1 * findAccount.find(accountId) >> Optional.empty()

        expect:
        mvc.perform(get("/accounts/$accountId"))
                .andExpect(status().isNotFound())
                .andReturn()
    }

    def "should return a list of account entries"() {
        given:
        1 * getAccountEntries.get(_ as Long, _ as Pageable) >> { Long id, PageRequest pageRequest ->
            new PageImpl<AccountEntryData>((1..50).collect({
                AccountEntryData.builder()
                        .accountId(id)
                        .transactionId(it * 16)
                        .currency("EUR")
                        .timestamp(OffsetDateTime.parse('2021-12-10T20:17:22.476322Z'))
                        .type(AccountEntryType.DEBIT)
                        .amount(it * 77.3)
                        .build()
            }), pageRequest, 50)
        }

        expect:
        mvc.perform(get("/accounts/1/entries?page=1&size=10"))
                .andExpect(status().isOk())
                .andExpect(content().json(getResponseExample('entries')))
                .andReturn()
    }

    def "should return status 400 if account is not found while listing entries"() {
        given:
        1 * getAccountEntries.get(_ as Long, _ as Pageable) >> { Long id, PageRequest pageRequest ->
            throw new AccountEntriesRetrievalException(id)
        }

        expect:
        mvc.perform(get("/accounts/1/entries?page=1&size=10"))
                .andExpect(status().isBadRequest())
                .andReturn()
    }

    def "should deactivate account"() {
        given:
        1 * deactivateAccount.deactivate(_ as Long) >> { Long id ->
            accountDataFixture(id: id)
        }

        expect:
        mvc.perform(delete("/accounts/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(getResponseExample('delete')))
                .andReturn()
    }

    def "should return status 404 if deactivating account does not exist"() {
        given:
        1 * deactivateAccount.deactivate(_ as Long) >> { Long id ->
            throw new AccountNotFoundException(id)
        }

        expect:
        mvc.perform(delete("/accounts/1"))
                .andExpect(status().isNotFound())
                .andReturn()
    }

    def "should update account"() {
        given:
        1 * updateAccount.update(_ as Long, _ as UpdateAccountRequest) >> { Long id, UpdateAccountRequest updateAccountRequest ->
            accountDataFixture(id: id)
        }

        expect:
        mvc.perform(patch("/accounts/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content('{"status": "DEACTIVATED"}'))
                .andExpect(status().isOk())
                .andExpect(content().json(getResponseExample('update')))
                .andReturn()
    }

    def "should return status code 404 if there is no account to update"() {
        given:
        1 * updateAccount.update(_ as Long, _ as UpdateAccountRequest) >> { Long id, UpdateAccountRequest updateAccountRequest ->
            throw new AccountNotFoundException(id)
        }

        expect:
        mvc.perform(patch("/accounts/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content('{"status": "DEACTIVATED"}'))
                .andExpect(status().isNotFound())
                .andReturn()
    }

    def "should create new account"() {
        given:
        1 * supportedCurrencies.getCurrencies() >> ["BTC", "EUR", "USD"]
        1 * openAccount.open(_ as OpenAccountRequest) >> { OpenAccountRequest openAccountRequest ->
            accountDataFixture(
                    clientId: openAccountRequest.clientId,
                    currency: openAccountRequest.currency
            )
        }

        expect:
        mvc.perform(post("/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content('{"clientId": 7, "currency": "BTC"}'))
                .andExpect(status().isOk())
                .andExpect(content().json(getResponseExample('create')))
                .andReturn()
    }

    def "should return status code 400 if client not found while creating account"() {
        given:
        1 * supportedCurrencies.getCurrencies() >> ["BTC", "EUR", "USD"]
        1 * openAccount.open(_ as OpenAccountRequest) >> { OpenAccountRequest openAccountRequest ->
            throw new ClientDoesntExistException(openAccountRequest.getClientId())
        }

        expect:
        mvc.perform(post("/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content('{"clientId": 7, "currency": "BTC"}'))
                .andExpect(status().isBadRequest())
                .andReturn()
    }

    def "should return status code 400 if currency is not supported"() {
        given:
        1 * supportedCurrencies.getCurrencies() >> ["EUR", "USD"]
        0 * openAccount.open(_ as OpenAccountRequest)

        expect:
        mvc.perform(post("/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content('{"clientId": 7, "currency": "BTC"}'))
                .andExpect(status().isBadRequest())
                .andReturn()
    }

    static String getResponseExample(String filename) {
        new File("src/integrationTest/resources/api-responses/accounts/${filename}.json").text
    }

}
