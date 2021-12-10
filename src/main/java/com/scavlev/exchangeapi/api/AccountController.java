package com.scavlev.exchangeapi.api;

import com.scavlev.exchangeapi.account.*;
import com.scavlev.exchangeapi.account.services.*;
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
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static reactor.core.publisher.Mono.just;

@Tag(name = "account")
@RestController
@RequestMapping("/accounts")
@AllArgsConstructor(access = AccessLevel.PACKAGE)
class AccountController {

    private final ListAccounts listAccounts;
    private final FindAccount findAccount;
    private final DeactivateAccount deactivateAccount;
    private final OpenAccount openAccount;
    private final GetAccountEntries getAccountEntries;
    private final UpdateAccount updateAccount;

    private final AccountDataModelAssembler accountDataModelAssembler;
    private final AccountEntryDataModelAssembler accountEntryDataModelAssembler;

    private final PagedResourcesAssembler<AccountData> accountDataPagedResourcesAssembler;
    private final PagedResourcesAssembler<AccountEntryData> accountEntryDataPagedResourcesAssembler;

    @Operation(summary = "List accounts")
    @GetMapping
    @PageableAsQueryParam
    PagedModel<EntityModel<AccountData>> listAccounts(@Parameter(hidden = true) @PageableDefault(value = 20) Pageable pageRequest) {
        return just(pageRequest)
                .map(listAccounts)
                .map(page -> accountDataPagedResourcesAssembler.toModel(page, accountDataModelAssembler))
                .block();
    }

    @Operation(summary = "Find account", responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404", description = "Account not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))})
    @GetMapping("/{id}")
    EntityModel<AccountData> getAccount(@PathVariable Long id) {
        return just(id)
                .map(findAccount)
                .map(optional -> optional.orElseThrow(() -> new AccountNotFoundException(id)))
                .map(accountDataModelAssembler::toModel)
                .block();
    }

    @Operation(summary = "List account entries", responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = ApiError.class)))})
    @PageableAsQueryParam
    @GetMapping("/{id}/entries")
    PagedModel<EntityModel<AccountEntryData>> getEntries(@PathVariable Long id, @Parameter(hidden = true) @PageableDefault(value = 20) Pageable pageRequest) {
        return just(getAccountEntries.apply(id, pageRequest))
                .map(page -> accountEntryDataPagedResourcesAssembler.toModel(page, accountEntryDataModelAssembler))
                .block();
    }

    @Operation(summary = "Deactivate account", responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404", description = "Account not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))})
    @DeleteMapping("/{id}")
    EntityModel<AccountData> deactivateAccount(@PathVariable Long id) {
        return just(id)
                .map(deactivateAccount)
                .map(accountDataModelAssembler::toModel)
                .block();
    }

    @Operation(summary = "Update account", responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404", description = "Account not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))})
    @PatchMapping("/{id}")
    EntityModel<AccountData> updateAccount(@PathVariable Long id, @Valid @RequestBody UpdateAccountRequest updateAccountRequest) {
        return just(updateAccount.apply(id, updateAccountRequest))
                .map(accountDataModelAssembler::toModel)
                .block();
    }

    @Operation(summary = "Open new account", responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = ApiError.class)))})
    @PostMapping
    EntityModel<AccountData> createAccount(@Valid @RequestBody OpenAccountRequest openAccountRequest) {
        return just(openAccountRequest)
                .map(openAccount)
                .map(accountDataModelAssembler::toModel)
                .block();
    }
}
