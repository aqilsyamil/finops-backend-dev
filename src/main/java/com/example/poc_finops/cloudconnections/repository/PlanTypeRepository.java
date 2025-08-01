package com.example.poc_finops.cloudconnections.repository;

import com.example.poc_finops.cloudconnections.domain.entity.PlanType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlanTypeRepository extends JpaRepository<PlanType, UUID> {
    List<PlanType> findByCspConnectionId(UUID cspConnectionId);
    Optional<PlanType> findByName(String name);
    List<PlanType> findByNameContainingIgnoreCase(String name);
}