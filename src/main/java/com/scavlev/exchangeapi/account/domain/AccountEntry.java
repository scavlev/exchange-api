package com.scavlev.exchangeapi.account.domain;

import com.scavlev.exchangeapi.domain.BaseEntity;
import com.scavlev.exchangeapi.transaction.domain.Transaction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SequenceGenerator(name = "sequence", sequenceName = "account_entry_seq", allocationSize = 1)
public class AccountEntry extends BaseEntity {

    @NotNull
    @ManyToOne
    private Account account;

    @NotNull
    @ManyToOne
    private Transaction transaction;

    @NotNull
    private BigDecimal amount;

    @NotNull
    @Enumerated(EnumType.STRING)
    private AccountEntryType type;

}
