package com.scavlev.exchangeapi.client.services

import com.scavlev.exchangeapi.IntegrationSpecification
import com.scavlev.exchangeapi.account.AccountData
import org.springframework.beans.factory.annotation.Autowired

class GetClientAccountsITSpec extends IntegrationSpecification {

    @Autowired
    GetClientAccounts getClientAccounts

    def "should get all client accounts"() {
        given:
        long clientId = 4

        when:
        List<AccountData> clientAccounts = getClientAccounts.get(clientId)

        then:
        clientAccounts.size() == 3
        clientAccounts.stream().allMatch({ it.clientId == clientId })
    }
}
