package com.scavlev.exchangeapi.client

import com.scavlev.exchangeapi.client.domain.Client
import spock.lang.Specification

import static com.scavlev.exchangeapi.FixtureHelper.createClient

class ClientDataSpec extends Specification {

    def "should correctly transform client data"() {
        given:
        Client client = createClient()

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
