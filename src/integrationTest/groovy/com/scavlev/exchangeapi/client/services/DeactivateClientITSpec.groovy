package com.scavlev.exchangeapi.client.services

import com.scavlev.exchangeapi.IntegrationSpecification
import com.scavlev.exchangeapi.client.ClientData
import com.scavlev.exchangeapi.client.domain.ClientRepository
import com.scavlev.exchangeapi.client.domain.ClientStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class DeactivateClientITSpec extends IntegrationSpecification {

    @Autowired
    ClientRepository clientRepository

    @Autowired
    DeactivateClient deactivateClient

    @Transactional
    def "should deactivate client by id"() {
        given:
        long clientId = 10

        when:
        ClientData deactivatedClient = deactivateClient.deactivate(clientId)

        then:
        deactivatedClient.status == ClientStatus.DEACTIVATED
        clientRepository.getById(clientId).status == ClientStatus.DEACTIVATED
    }
}
