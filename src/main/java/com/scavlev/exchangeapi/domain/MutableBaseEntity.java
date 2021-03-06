package com.scavlev.exchangeapi.domain;

import lombok.Getter;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
import java.time.OffsetDateTime;

@Getter
@MappedSuperclass
public abstract class MutableBaseEntity extends BaseEntity {

    @Version
    private Integer version;

    @UpdateTimestamp
    private OffsetDateTime entityUpdated;
}
