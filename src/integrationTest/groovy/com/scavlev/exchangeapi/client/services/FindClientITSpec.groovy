package com.scavlev.exchangeapi.client.services

import com.scavlev.exchangeapi.IntegrationSpecification
import com.scavlev.exchangeapi.client.ClientData
import com.scavlev.exchangeapi.client.domain.ClientStatus
import org.springframework.beans.factory.annotation.Autowired

class FindClientITSpec extends IntegrationSpecification {

    @Autowired
    FindClient findClient

    def "should find a client by id"() {
        given:
        long clientId = 2

        when:
        Optional<ClientData> clientData = findClient.find(clientId)

        then:
        clientData.present
        with(clientData.get()) {
            id == clientId
            status == ClientStatus.ACTIVE
        }
    }
}
