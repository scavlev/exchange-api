package com.scavlev.exchangeapi.api;

import com.scavlev.exchangeapi.transaction.TransactionData;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
class TransactionDataModelAssembler implements RepresentationModelAssembler<TransactionData, EntityModel<TransactionData>> {

    @Override
    public EntityModel<TransactionData> toModel(TransactionData transactionData) {
        List<Link> links = new ArrayList<>();

        links.add(linkTo(methodOn(TransactionController.class, transactionData.getId())
                .getTransaction(transactionData.getId()))
                .withSelfRel());

        links.add(linkTo(methodOn(TransactionController.class, transactionData.getId())
                .getTransactionEntries(transactionData.getId()))
                .withRel("entries"));

        transactionData.getFromAccount()
                .map(fromAccount -> linkTo(methodOn(AccountController.class, fromAccount.getId())
                        .getAccount(fromAccount.getId()))
                        .withRel("from-account"))
                .ifPresent(links::add);

        transactionData.getToAccount()
                .map(toAccount -> linkTo(methodOn(AccountController.class, toAccount.getId())
                        .getAccount(toAccount.getId()))
                        .withRel("to-account"))
                .ifPresent(links::add);

        return EntityModel.of(transactionData, links);
    }

}
