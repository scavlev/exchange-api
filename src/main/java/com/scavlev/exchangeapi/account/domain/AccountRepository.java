package com.scavlev.exchangeapi.account.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AccountRepository extends JpaRepository<Account, Long> {

    @Modifying
    @Query("update Account a set a.status = 'DEACTIVATED' where a.id = :id")
    void deactivateAccount(@Param("id") Long id);

    @Modifying
    @Query("update Account a set a.status = 'DEACTIVATED' where a.client.id = :clientId")
    void deactivateClientAccounts(@Param("clientId") Long clientId);
}
