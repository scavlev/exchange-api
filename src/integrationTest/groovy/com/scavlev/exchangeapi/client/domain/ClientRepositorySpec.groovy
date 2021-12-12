package com.scavlev.exchangeapi.client.domain

import com.scavlev.exchangeapi.IntegrationSpecification
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class ClientRepositorySpec extends IntegrationSpecification {

    @Autowired
    ClientRepository clientRepository

    @Transactional
    def "should deactivate client by id"() {
        given:
        def clientId = 1L

        when:
        clientRepository.deactivateClient(clientId)

        then:
        clientRepository.getById(clientId).status == ClientStatus.DEACTIVATED
    }

}
