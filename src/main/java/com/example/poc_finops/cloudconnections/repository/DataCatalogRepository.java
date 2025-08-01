package com.example.poc_finops.cloudconnections.repository;

import com.example.poc_finops.cloudconnections.domain.entity.DataCatalog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DataCatalogRepository extends JpaRepository<DataCatalog, UUID> {
    List<DataCatalog> findByCspConnectionId(UUID cspConnectionId);
    Optional<DataCatalog> findByName(String name);
}