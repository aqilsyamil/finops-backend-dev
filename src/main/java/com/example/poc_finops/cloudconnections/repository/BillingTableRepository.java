package com.example.poc_finops.cloudconnections.repository;

import com.example.poc_finops.cloudconnections.domain.entity.BillingTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BillingTableRepository extends JpaRepository<BillingTable, UUID> {
    List<BillingTable> findByCspConnectionId(UUID cspConnectionId);
    Optional<BillingTable> findByName(String name);
}