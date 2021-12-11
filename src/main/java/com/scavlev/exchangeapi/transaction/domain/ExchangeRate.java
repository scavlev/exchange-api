package com.scavlev.exchangeapi.transaction.domain;

import com.scavlev.exchangeapi.domain.BaseEntity;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SequenceGenerator(name = "sequence", sequenceName = "exchange_rate_seq", allocationSize = 1)
public class ExchangeRate extends BaseEntity {

    @OneToOne(optional = false)
    private Transaction transaction;

    @NotNull
    private String fromCurrency;

    @NotNull
    private String toCurrency;

    @NotNull
    private BigDecimal rate;

}
