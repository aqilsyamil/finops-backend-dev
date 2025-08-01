package com.example.poc_finops.cloudconnections.repository;

import com.example.poc_finops.cloudconnections.domain.entity.GroupConnection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GroupConnectionRepository extends JpaRepository<GroupConnection, UUID> {
    List<GroupConnection> findByOrganizationId(UUID organizationId);
    Optional<GroupConnection> findByName(String name);
    List<GroupConnection> findByNameContainingIgnoreCase(String name);
}