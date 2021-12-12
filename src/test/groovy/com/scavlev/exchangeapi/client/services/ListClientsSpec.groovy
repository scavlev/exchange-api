package com.scavlev.exchangeapi.client.services

import com.scavlev.exchangeapi.client.ClientData
import com.scavlev.exchangeapi.client.domain.ClientRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import spock.lang.Specification

import static com.scavlev.exchangeapi.FixtureHelper.createClient

class ListClientsSpec extends Specification {

    ClientRepository clientRepository = Mock()
    ListClients listClients = new ListClients(clientRepository)

    def "should return a page of client data"() {
        given:
        def pageRequest = PageRequest.of(0, 10)
        def clients = (1..10).collect {
            createClient(id: it)
        }

        when:
        Page<ClientData> clientData = listClients.list(pageRequest)

        then:
        1 * clientRepository.findAll(pageRequest) >> new PageImpl<>(clients)
        clientData.size() == clients.size()
    }

}
