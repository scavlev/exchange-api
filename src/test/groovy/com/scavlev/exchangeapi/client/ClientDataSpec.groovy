package com.scavlev.exchangeapi.client


import com.scavlev.exchangeapi.client.domain.Client
import com.scavlev.exchangeapi.client.domain.ClientStatus
import spock.lang.Specification

class ClientDataSpec extends Specification {

    def "should correctly transform client data"() {
        given:
        Client client = Mock()
        client.id >> 1
        client.status >> ClientStatus.ACTIVE

        when:
        ClientData clientData = ClientData.fromClient(client)

        then:
        clientData != null
        with(clientData) {
            id == client.id
            status == client.status
        }
    }

}
