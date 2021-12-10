package com.scavlev.exchangeapi.api;

import com.scavlev.exchangeapi.account.AccountEntryData;
import com.scavlev.exchangeapi.transaction.*;
import com.scavlev.exchangeapi.transaction.services.*;
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

@Tag(name = "transaction")
@RestController
@RequestMapping("/transactions")
@AllArgsConstructor(access = AccessLevel.PACKAGE)
class TransactionController {

    private final ProcessTransaction processTransaction;
    private final DepositFunds depositFunds;
    private final WithdrawFunds withdrawFunds;
    private final ListTransactions listTransactions;
    private final FindTransaction findTransaction;
    private final GetTransactionEntries getTransactionEntries;

    private final AccountEntryDataModelAssembler accountEntryDataModelAssembler;
    private final TransactionDataModelAssembler transactionDataModelAssembler;
    private final PagedResourcesAssembler<TransactionData> transactionDataPagedResourcesAssembler;

    @Operation(summary = "List transactions")
    @GetMapping
    @PageableAsQueryParam
    PagedModel<EntityModel<TransactionData>> listTransactions(@Parameter(hidden = true) @PageableDefault(value = 20) Pageable pageRequest) {
        return just(pageRequest)
                .map(listTransactions)
                .map(page -> transactionDataPagedResourcesAssembler.toModel(page, transactionDataModelAssembler))
                .block();
    }

    @Operation(summary = "Find transaction", responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404", description = "Transaction not found", content = @Content())})
    @GetMapping("/{id}")
    EntityModel<TransactionData> getTransaction(@PathVariable Long id) {
        return just(id)
                .map(findTransaction)
                .map(optional -> optional.orElseThrow(() -> new TransactionNotFoundException(id)))
                .map(transactionDataModelAssembler::toModel)
                .block();
    }

    @Operation(summary = "Get transaction entries", responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = ApiError.class)))})
    @GetMapping("/{id}/entries")
    CollectionModel<EntityModel<AccountEntryData>> getTransactionEntries(@PathVariable Long id) {
        return just(getTransactionEntries.apply(id))
                .map(accountEntryDataModelAssembler::toCollectionModel)
                .block();
    }

    @Operation(summary = "Process transfer transaction", responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = ApiError.class)))})
    @PostMapping
    EntityModel<TransactionData> processTransaction(@Valid @RequestBody ProcessTransactionRequest processTransactionRequest) {
        return just(processTransactionRequest)
                .map(processTransaction)
                .map(transactionDataModelAssembler::toModel)
                .block();
    }

    @Operation(summary = "Process deposit transaction", responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = ApiError.class)))})
    @PostMapping("/deposit")
    EntityModel<TransactionData> depositFunds(@Valid @RequestBody DepositTransactionRequest depositTransactionRequest) {
        return just(depositTransactionRequest)
                .map(depositFunds)
                .map(transactionDataModelAssembler::toModel)
                .block();
    }

    @Operation(summary = "Process withdrawal transaction", responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = ApiError.class)))})
    @PostMapping("/withdrawal")
    EntityModel<TransactionData> withdrawFunds(@Valid @RequestBody WithdrawalTransactionRequest withdrawalTransactionRequest) {
        return just(withdrawalTransactionRequest)
                .map(withdrawFunds)
                .map(transactionDataModelAssembler::toModel)
                .block();
    }

}
