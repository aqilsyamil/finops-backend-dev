package com.example.poc_finops.cloudconnections.repository;

import com.example.poc_finops.cloudconnections.domain.entity.GroupsConnections;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GroupsConnectionsRepository extends JpaRepository<GroupsConnections, UUID> {
    List<GroupsConnections> findByGroupConnectionId(UUID groupConnectionId);
    List<GroupsConnections> findByCspConnectionId(UUID cspConnectionId);
}