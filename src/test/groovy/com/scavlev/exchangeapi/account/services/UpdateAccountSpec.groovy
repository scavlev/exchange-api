package com.scavlev.exchangeapi.account.services

import com.scavlev.exchangeapi.account.AccountData
import com.scavlev.exchangeapi.account.AccountNotFoundException
import com.scavlev.exchangeapi.account.UpdateAccountRequest
import com.scavlev.exchangeapi.account.domain.Account
import com.scavlev.exchangeapi.account.domain.AccountRepository
import com.scavlev.exchangeapi.account.domain.AccountStatus
import spock.lang.Specification

import static com.scavlev.exchangeapi.FixtureHelper.createAccount

class UpdateAccountSpec extends Specification {

    AccountRepository accountRepository = Mock()
    UpdateAccount updateAccount = new UpdateAccount(accountRepository)

    def "should update account with request data"() {
        given:
        long accountId = 1
        UpdateAccountRequest updateAccountRequest = new UpdateAccountRequest(AccountStatus.ACTIVE)
        Account existingAccount = createAccount(id: accountId, status: AccountStatus.DEACTIVATED)

        when:
        AccountData accountData = updateAccount.update(accountId, updateAccountRequest)

        then:
        1 * accountRepository.findById(accountId) >> Optional.of(existingAccount)
        1 * accountRepository.saveAndFlush(_) >> { Account account ->
            assert account.status == updateAccountRequest.status
            account
        }
        accountData != null
        accountData.status == updateAccountRequest.status
    }

    def "should throw exception if account is not found"() {
        given:
        long accountId = 1
        UpdateAccountRequest updateAccountRequest = new UpdateAccountRequest(AccountStatus.ACTIVE)

        when:
        updateAccount.update(accountId, updateAccountRequest)

        then:
        1 * accountRepository.findById(accountId) >> Optional.empty()
        thrown(AccountNotFoundException)
    }

}
