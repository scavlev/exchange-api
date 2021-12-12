package com.scavlev.exchangeapi.client.services


import com.scavlev.exchangeapi.client.ClientData
import com.scavlev.exchangeapi.client.ClientNotFoundException
import com.scavlev.exchangeapi.client.UpdateClientRequest
import com.scavlev.exchangeapi.client.domain.Client
import com.scavlev.exchangeapi.client.domain.ClientRepository
import com.scavlev.exchangeapi.client.domain.ClientStatus
import spock.lang.Specification

import static com.scavlev.exchangeapi.FixtureHelper.createClient

class UpdateClientSpec extends Specification {

    ClientRepository clientRepository = Mock()
    UpdateClient updateClient = new UpdateClient(clientRepository)

    def "should update client with request data"() {
        given:
        def clientId = 1
        UpdateClientRequest updateClientRequest = new UpdateClientRequest(ClientStatus.ACTIVE)
        Client existingClient = createClient(status: ClientStatus.DEACTIVATED)

        when:
        ClientData clientData = updateClient.update(clientId, updateClientRequest)

        then:
        1 * clientRepository.findById(clientId) >> Optional.of(existingClient)
        1 * clientRepository.saveAndFlush(_) >> { Client client ->
            assert client.status == updateClientRequest.status
            client
        }
        clientData != null
        clientData.status == updateClientRequest.status
    }

    def "should throw exception if client is not found"() {
        given:
        def clientId = 1
        UpdateClientRequest updateClientRequest = new UpdateClientRequest(ClientStatus.ACTIVE)

        when:
        updateClient.update(clientId, updateClientRequest)

        then:
        1 * clientRepository.findById(clientId) >> Optional.empty()
        thrown(ClientNotFoundException)
    }

}