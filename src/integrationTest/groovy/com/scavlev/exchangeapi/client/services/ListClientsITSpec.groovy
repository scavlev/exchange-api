package com.scavlev.exchangeapi.client.services

import com.scavlev.exchangeapi.IntegrationSpecification
import com.scavlev.exchangeapi.client.ClientData
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest

class ListClientsITSpec extends IntegrationSpecification {

    @Autowired
    ListClients listClients

    def "should return a page of clients"() {
        when:
        Page<ClientData> pageOfClients = listClients.list(PageRequest.of(0, 5))

        then:
        pageOfClients.numberOfElements == 5
        pageOfClients.totalElements >= 10
    }
}
