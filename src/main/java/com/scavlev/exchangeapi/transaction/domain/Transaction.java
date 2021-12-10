package com.scavlev.exchangeapi.transaction.domain;

import com.scavlev.exchangeapi.account.domain.AccountEntry;
import com.scavlev.exchangeapi.account.domain.AccountEntryType;
import com.scavlev.exchangeapi.domain.BaseEntity;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SequenceGenerator(name = "sequence", sequenceName = "transaction_seq", allocationSize = 1)
public class Transaction extends BaseEntity {

    @OneToMany(mappedBy = "transaction", fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST})
    private final List<AccountEntry> entries = new ArrayList<>();
    @NotNull
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;
    @Setter
    @OneToOne(mappedBy = "transaction", cascade = {CascadeType.PERSIST})
    private ExchangeRate exchangeRate;

    public void addEntry(AccountEntry accountEntry) {
        this.entries.add(accountEntry);
    }

    public Optional<ExchangeRate> getExchangeRate() {
        return Optional.ofNullable(exchangeRate);
    }

    public Optional<AccountEntry> getDebitAccountEntry() {
        return entries.stream().filter(accountEntry -> accountEntry.getType() == AccountEntryType.DEBIT).findFirst();
    }

    public Optional<AccountEntry> getCreditAccountEntry() {
        return entries.stream().filter(accountEntry -> accountEntry.getType() == AccountEntryType.CREDIT).findFirst();
    }
}
