package com.scavlev.exchangeapi.client.domain;

import com.scavlev.exchangeapi.account.domain.Account;
import com.scavlev.exchangeapi.domain.MutableBaseEntity;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SequenceGenerator(name = "sequence", sequenceName = "client_seq", allocationSize = 1)
public class Client extends MutableBaseEntity {

    @OneToMany(mappedBy = "client")
    private final List<Account> accounts = new ArrayList<>();

    @NotNull
    @Setter
    @Enumerated(EnumType.STRING)
    private ClientStatus status;

}
