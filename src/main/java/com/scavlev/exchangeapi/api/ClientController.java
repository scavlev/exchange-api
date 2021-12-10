package com.scavlev.exchangeapi.api;

import com.scavlev.exchangeapi.account.AccountData;
import com.scavlev.exchangeapi.client.ClientData;
import com.scavlev.exchangeapi.client.ClientNotFoundException;
import com.scavlev.exchangeapi.client.UpdateClientRequest;
import com.scavlev.exchangeapi.client.services.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static reactor.core.publisher.Mono.just;

@Tag(name = "client")
@RestController
@RequestMapping("/clients")
@AllArgsConstructor(access = AccessLevel.PACKAGE)
class ClientController {

    private final ListClients listClients;
    private final FindClient findClient;
    private final RegisterClient registerClient;
    private final DeactivateClient deactivateClient;
    private final GetClientAccounts getClientAccounts;
    private final UpdateClient updateClient;

    private final ClientDataModelAssembler clientDataModelAssembler;
    private final AccountDataModelAssembler accountDataModelAssembler;

    private final PagedResourcesAssembler<ClientData> clientDataPagedResourcesAssembler;

    @Operation(summary = "List clients")
    @GetMapping
    @PageableAsQueryParam
    PagedModel<EntityModel<ClientData>> listClients(@Parameter(hidden = true) @PageableDefault(value = 20) Pageable pageRequest) {
        return just(pageRequest)
                .map(listClients)
                .map(page -> clientDataPagedResourcesAssembler.toModel(page, clientDataModelAssembler))
                .block();
    }

    @Operation(summary = "Find client", responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404", description = "Client not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))})
    @GetMapping("/{id}")
    EntityModel<ClientData> getClient(@PathVariable Long id) {
        return just(id)
                .map(findClient)
                .map(optional -> optional.orElseThrow(() -> new ClientNotFoundException(id)))
                .map(clientDataModelAssembler::toModel)
                .block();
    }

    @Operation(summary = "Get client accounts", responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = ApiError.class)))})
    @GetMapping("/{id}/accounts")
    CollectionModel<EntityModel<AccountData>> getClientAccounts(@PathVariable Long id) {
        return just(id)
                .map(getClientAccounts)
                .map(accountDataModelAssembler::toCollectionModel)
                .block();
    }

    @Operation(summary = "Deactivate client", responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404", description = "Client not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))})
    @DeleteMapping("/{id}")
    EntityModel<ClientData> deactivateClient(@PathVariable Long id) {
        return just(id)
                .map(deactivateClient)
                .map(clientDataModelAssembler::toModel)
                .block();
    }

    @Operation(summary = "Update client", responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404", description = "Client not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))})
    @PatchMapping("/{id}")
    EntityModel<ClientData> updateClient(@PathVariable Long id, @Valid @RequestBody UpdateClientRequest updateClientRequest) {
        return just(updateClient.apply(id, updateClientRequest))
                .map(clientDataModelAssembler::toModel)
                .block();
    }

    @Operation(summary = "Register new client")
    @PostMapping
    EntityModel<ClientData> registerClient() {
        return clientDataModelAssembler.toModel(registerClient.call());
    }

}
