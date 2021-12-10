package com.scavlev.exchangeapi.api;

import com.scavlev.exchangeapi.client.ClientData;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
class ClientDataModelAssembler implements RepresentationModelAssembler<ClientData, EntityModel<ClientData>> {

    @Override
    public EntityModel<ClientData> toModel(ClientData clientData) {
        return EntityModel.of(clientData,
                linkTo(methodOn(ClientController.class, clientData.getId()).getClient(clientData.getId()))
                        .withSelfRel(),
                linkTo(methodOn(ClientController.class, clientData.getId()).getClientAccounts(clientData.getId()))
                        .withRel("accounts"));
    }

}
