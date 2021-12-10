package com.scavlev.exchangeapi.api;

import com.scavlev.exchangeapi.account.AccountData;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.data.domain.Pageable.unpaged;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
class AccountDataModelAssembler implements RepresentationModelAssembler<AccountData, EntityModel<AccountData>> {

    @Override
    public EntityModel<AccountData> toModel(AccountData accountData) {
        return EntityModel.of(accountData,
                linkTo(methodOn(AccountController.class, accountData.getId()).getAccount(accountData.getId()))
                        .withSelfRel(),
                linkTo(methodOn(AccountController.class, accountData.getId())
                        .getEntries(accountData.getId(), unpaged()))
                        .withRel("entries"),
                linkTo(methodOn(ClientController.class, accountData.getClientId()).getClient(accountData.getClientId()))
                        .withRel("client"));
    }

}
