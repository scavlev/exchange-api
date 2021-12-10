package com.scavlev.exchangeapi.client.services

import com.scavlev.exchangeapi.account.AccountData
import com.scavlev.exchangeapi.account.domain.Account
import com.scavlev.exchangeapi.account.domain.AccountStatus
import com.scavlev.exchangeapi.client.ClientDoesntExistException
import com.scavlev.exchangeapi.client.domain.Client
import com.scavlev.exchangeapi.client.domain.ClientRepository
import spock.lang.Specification

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

        Client client = Mock()
        client.id >> clientId

        Account account1 = Mock()
        account1.id >> 1
        account1.client >> client
        account1.currency >> "EUR"
        account1.balance >> 43
        account1.status >> AccountStatus.ACTIVE

        Account account2 = Mock()
        account2.id >> 2
        account2.client >> client
        account2.currency >> "USD"
        account2.balance >> 23
        account2.status >> AccountStatus.DEACTIVATED

        client.accounts >> [account1, account2]

        when:
        List<AccountData> accountDataList = getClientAccounts.apply(clientId)

        then:
        1 * clientRepository.findById(clientId) >> Optional.of(client)
        accountDataList != null
        accountDataList.size() == 2
        accountDataList[0].id == account1.id
        accountDataList[0].clientId == client.id
        accountDataList[0].currency == account1.currency
        accountDataList[0].balance == account1.balance
        accountDataList[0].status == account1.status
        accountDataList[1].id == account2.id
        accountDataList[1].clientId == client.id
        accountDataList[1].currency == account2.currency
        accountDataList[1].balance == account2.balance
        accountDataList[1].status == account2.status
    }
}
