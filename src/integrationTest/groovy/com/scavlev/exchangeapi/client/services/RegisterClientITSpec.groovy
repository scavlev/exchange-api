package com.scavlev.exchangeapi.client.services

import com.scavlev.exchangeapi.IntegrationSpecification
import com.scavlev.exchangeapi.client.ClientData
import com.scavlev.exchangeapi.client.domain.Client
import com.scavlev.exchangeapi.client.domain.ClientRepository
import com.scavlev.exchangeapi.client.domain.ClientStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class RegisterClientITSpec extends IntegrationSpecification {

    @Autowired
    ClientRepository clientRepository

    @Autowired
    RegisterClient registerClient

    @Transactional
    def "should successfully create new client"() {
        when:
        ClientData registeredClient = registerClient.register()

        then:
        Client dbClient = clientRepository.getById(registeredClient.id)
        with(registeredClient) {
            id != null
            status == ClientStatus.ACTIVE
        }
        with(dbClient) {
            id == registeredClient.id
            status == ClientStatus.ACTIVE
        }
    }
}
