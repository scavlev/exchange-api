package com.scavlev.exchangeapi.account.domain;

import com.scavlev.exchangeapi.client.domain.Client;
import com.scavlev.exchangeapi.domain.MutableBaseEntity;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SequenceGenerator(name = "sequence", sequenceName = "account_seq", allocationSize = 1)
public class Account extends MutableBaseEntity {

    @OneToMany(mappedBy = "account")
    private final List<AccountEntry> entries = new ArrayList<>();
    @NotNull
    @ManyToOne
    private Client client;
    @NotNull
    @PositiveOrZero
    private BigDecimal balance;
    @NotNull
    private String currency;
    @NotNull
    @Setter
    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    public boolean isDeactivated() {
        return this.status == AccountStatus.DEACTIVATED;
    }

    public void alterBalanceBy(BigDecimal delta) {
        this.balance = this.balance.add(delta);
    }

}
