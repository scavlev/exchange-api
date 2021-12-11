package com.scavlev.exchangeapi.client.services

import com.scavlev.exchangeapi.account.AccountData
import com.scavlev.exchangeapi.client.ClientDoesntExistException
import com.scavlev.exchangeapi.client.domain.Client
import com.scavlev.exchangeapi.client.domain.ClientRepository
import spock.lang.Specification

import static com.scavlev.exchangeapi.FixtureHelper.createAccount
import static com.scavlev.exchangeapi.FixtureHelper.createClient
import static com.scavlev.exchangeapi.account.AccountData.fromAccount

class GetClientAccountsSpec extends Specification {

    ClientRepository clientRepository = Mock()
    GetClientAccounts getClientAccounts = new GetClientAccounts(clientRepository)

    def "should fail if client doesn't exist"() {
        given:
        def clientId = 1

        when:
        getClientAccounts.apply(clientId)

        then:
        1 * clientRepository.findById(clientId) >> Optional.empty()
        thrown(ClientDoesntExistException)
    }

    def "should return all client account data"() {
        given:
        def clientId = 1

        Client client = createClient(id: clientId).with {
            accounts.addAll(
                    createAccount(id: 1, client: it),
                    createAccount(id: 2, client: it)
            )
            it
        }

        when:
        List<AccountData> accountDataList = getClientAccounts.apply(clientId)

        then:
        1 * clientRepository.findById(clientId) >> Optional.of(client)
        accountDataList != null
        accountDataList.size() == 2
        accountDataList[0] == fromAccount(client.accounts[0])
        accountDataList[1] == fromAccount(client.accounts[1])
    }
}
