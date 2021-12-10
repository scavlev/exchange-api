package com.scavlev.exchangeapi.client.services


import com.scavlev.exchangeapi.client.domain.Client
import com.scavlev.exchangeapi.client.domain.ClientRepository
import com.scavlev.exchangeapi.client.domain.ClientStatus
import spock.lang.Specification

class FindClientSpec extends Specification {

    ClientRepository clientRepository = Mock()
    FindClient findClient = new FindClient(clientRepository)

    def "should return empty optional if client is not found"() {
        given:
        def clientId = 1

        when:
        def clientData = findClient.apply(clientId)

        then:
        1 * clientRepository.findById(clientId) >> Optional.empty()
        clientData.isEmpty()
    }

    def "should return client data if client is found"() {
        given:
        def clientId = 1
        Client client = Mock()
        client.id >> 1
        client.status >> ClientStatus.ACTIVE

        when:
        def clientData = findClient.apply(clientId)

        then:
        1 * clientRepository.findById(clientId) >> Optional.of(client)
        clientData.present
        with(clientData.get()) {
            id == client.id
            status == client.status
        }
    }

}