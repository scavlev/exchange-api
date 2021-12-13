package com.scavlev.exchangeapi.client.services

import com.scavlev.exchangeapi.client.ClientData
import com.scavlev.exchangeapi.client.domain.Client
import com.scavlev.exchangeapi.client.domain.ClientRepository
import com.scavlev.exchangeapi.client.domain.ClientStatus
import spock.lang.Specification

class RegisterClientSpec extends Specification {

    ClientRepository clientRepository = Mock()
    RegisterClient registerClient = new RegisterClient(clientRepository)

    def "should create and store new client"() {
        when:
        ClientData clientData = registerClient.register()

        then:
        1 * clientRepository.saveAndFlush(_) >> { Client client ->
            assert client.status == ClientStatus.ACTIVE
            client
        }
        clientData != null
        with(clientData) {
            status == ClientStatus.ACTIVE
        }
    }

}
