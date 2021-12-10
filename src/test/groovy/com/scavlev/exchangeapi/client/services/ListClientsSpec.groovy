package com.scavlev.exchangeapi.client.services


import com.scavlev.exchangeapi.client.ClientData
import com.scavlev.exchangeapi.client.domain.Client
import com.scavlev.exchangeapi.client.domain.ClientRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import spock.lang.Specification

class ListClientsSpec extends Specification {

    ClientRepository clientRepository = Mock()
    ListClients listClients = new ListClients(clientRepository)

    def "should return a page of client data"() {
        given:
        def pageRequest = PageRequest.of(0, 10)
        def clients = generateClients()

        when:
        Page<ClientData> clientData = listClients.apply(pageRequest)

        then:
        1 * clientRepository.findAll(pageRequest) >> new PageImpl<>(clients)
        clientData.size() == clients.size()
    }

    def generateClients() {
        (1..10).collect() {
            Client client = Mock()
            client
        }
    }

}
