package com.scavlev.exchangeapi.account.services

import com.scavlev.exchangeapi.account.AccountData
import com.scavlev.exchangeapi.account.OpenAccountRequest
import com.scavlev.exchangeapi.account.domain.Account
import com.scavlev.exchangeapi.account.domain.AccountRepository
import com.scavlev.exchangeapi.account.domain.AccountStatus
import com.scavlev.exchangeapi.client.ClientDoesntExistException
import com.scavlev.exchangeapi.client.domain.Client
import com.scavlev.exchangeapi.client.domain.ClientRepository
import spock.lang.Specification

import static com.scavlev.exchangeapi.FixtureHelper.createClient

class OpenAccountSpec extends Specification {

    AccountRepository accountRepository = Mock()
    ClientRepository clientRepository = Mock()
    OpenAccount openAccount = new OpenAccount(accountRepository, clientRepository)

    def "should create new account"() {
        given:
        long clientId = 1
        OpenAccountRequest request = new OpenAccountRequest(clientId, "EUR")
        Client client = createClient()

        when:
        AccountData accountData = openAccount.open(request)

        then:
        1 * clientRepository.findById(clientId) >> Optional.of(client)
        1 * accountRepository.saveAndFlush(_) >> { Account account ->
            assert account.client == client
            assert account.currency == request.currency
            assert account.balance == BigDecimal.ZERO
            assert account.status == AccountStatus.ACTIVE
            account
        }
        accountData != null
        with(accountData) {
            it.clientId == client.id
            currency == request.currency
            balance == BigDecimal.ZERO
            status == AccountStatus.ACTIVE
        }
    }

    def "should throw exception if client is not found"() {
        given:
        long clientId = 1
        OpenAccountRequest request = new OpenAccountRequest(clientId, "EUR")

        when:
        openAccount.open(request)

        then:
        1 * clientRepository.findById(clientId) >> Optional.empty()
        thrown(ClientDoesntExistException)
    }

}
