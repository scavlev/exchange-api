package com.scavlev.exchangeapi.client.services


import com.scavlev.exchangeapi.account.domain.AccountRepository
import com.scavlev.exchangeapi.client.ClientNotFoundException
import com.scavlev.exchangeapi.client.domain.Client
import com.scavlev.exchangeapi.client.domain.ClientRepository
import spock.lang.Specification

class DeactivateClientSpec extends Specification {

    ClientRepository clientRepository = Mock()
    AccountRepository accountRepository = Mock()
    DeactivateClient deactivateClient = new DeactivateClient(clientRepository, accountRepository)

    def "should deactivate client and all its accounts"() {
        given:
        def clientId = 1
        Client client = Mock()


        when:
        deactivateClient.apply(clientId)

        then:
        1 * clientRepository.findById(clientId) >> Optional.of(client)
        1 * clientRepository.deactivateClient(clientId)
        1 * accountRepository.deactivateClientAccounts(clientId)
    }

    def "should throw exception if client is not found"() {
        given:
        def clientId = 1

        when:
        deactivateClient.apply(clientId)

        then:
        1 * clientRepository.findById(clientId) >> Optional.empty()
        thrown(ClientNotFoundException)
    }

}
