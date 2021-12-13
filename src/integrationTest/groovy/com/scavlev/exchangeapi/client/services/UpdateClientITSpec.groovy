package com.scavlev.exchangeapi.client.services

import com.scavlev.exchangeapi.IntegrationSpecification
import com.scavlev.exchangeapi.client.ClientData
import com.scavlev.exchangeapi.client.UpdateClientRequest
import com.scavlev.exchangeapi.client.domain.ClientRepository
import com.scavlev.exchangeapi.client.domain.ClientStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class UpdateClientITSpec extends IntegrationSpecification {

    @Autowired
    ClientRepository clientRepository

    @Autowired
    UpdateClient updateClient

    @Transactional
    def "should update client record"() {
        given:
        long clientId = 5

        when:
        ClientData updatedClient = updateClient.update(clientId, new UpdateClientRequest(ClientStatus.DEACTIVATED))

        then:
        updatedClient.status == ClientStatus.DEACTIVATED
        clientRepository.getById(clientId).status == ClientStatus.DEACTIVATED
    }
}
