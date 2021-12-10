package com.scavlev.exchangeapi.api;

import com.scavlev.exchangeapi.account.AccountEntryData;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
class AccountEntryDataModelAssembler implements RepresentationModelAssembler<AccountEntryData, EntityModel<AccountEntryData>> {

    @Override
    public EntityModel<AccountEntryData> toModel(AccountEntryData accountEntryData) {
        return EntityModel.of(accountEntryData,
                linkTo(methodOn(AccountController.class, accountEntryData.getAccountId())
                        .getAccount(accountEntryData.getAccountId()))
                        .withRel("account"),
                linkTo(methodOn(TransactionController.class, accountEntryData.getTransactionId())
                        .getTransaction(accountEntryData.getTransactionId()))
                        .withRel("transaction"));
    }

}
