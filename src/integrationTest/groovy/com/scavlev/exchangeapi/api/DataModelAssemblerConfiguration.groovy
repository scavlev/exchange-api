package com.scavlev.exchangeapi.api

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration
class DataModelAssemblerConfiguration {

    @Bean
    ClientDataModelAssembler clientDataModelAssembler() {
        new ClientDataModelAssembler()
    }

    @Bean
    AccountDataModelAssembler accountDataModelAssembler() {
        new AccountDataModelAssembler()
    }

    @Bean
    AccountEntryDataModelAssembler accountEntryDataModelAssembler() {
        new AccountEntryDataModelAssembler()
    }

    @Bean
    TransactionDataModelAssembler transactionDataModelAssembler() {
        new TransactionDataModelAssembler()
    }

}
