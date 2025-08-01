package com.example.poc_finops.cloudconnections.repository;

import com.example.poc_finops.cloudconnections.domain.entity.CspConnection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CspConnectionRepository extends JpaRepository<CspConnection, UUID> {
    List<CspConnection> findByOrganizationId(UUID organizationId);
    List<CspConnection> findByCspId(UUID cspId);
    List<CspConnection> findByDataSourceId(UUID dataSourceId);
    List<CspConnection> findByPlanTypeId(UUID planTypeId);
    List<CspConnection> findByRegionId(UUID regionId);
    List<CspConnection> findByName(String name);
}