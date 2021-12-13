package com.scavlev.exchangeapi.transaction.services

import com.scavlev.exchangeapi.IntegrationSpecification
import com.scavlev.exchangeapi.transaction.domain.TransactionRepository
import org.springframework.beans.factory.annotation.Autowired

class GetTransactionEntriesITSpec extends IntegrationSpecification {

    @Autowired
    TransactionRepository transactionRepository

    @Autowired
    GetTransactionEntries getTransactionEntries
}
