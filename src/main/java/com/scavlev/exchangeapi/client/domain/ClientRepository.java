package com.scavlev.exchangeapi.client.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ClientRepository extends JpaRepository<Client, Long> {

    @Modifying
    @Query("update Client c set c.status = 'DEACTIVATED' where c.id = :id")
    void deactivateClient(@Param("id") Long id);

}
