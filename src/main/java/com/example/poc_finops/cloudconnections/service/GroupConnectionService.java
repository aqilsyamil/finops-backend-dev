package com.example.poc_finops.cloudconnections.service;

import com.example.poc_finops.cloudconnections.domain.entity.GroupConnection;

import java.util.List;
import java.util.UUID;

public interface GroupConnectionService {
    List<GroupConnection> getAllGroupConnections();
    List<GroupConnection> getGroupConnectionsByOrganization(UUID organizationId);
    GroupConnection getGroupConnectionById(UUID id);
    GroupConnection createGroupConnection(String name, UUID organizationId);
    GroupConnection updateGroupConnection(UUID id, String name);
    void deleteGroupConnection(UUID id);
    void addConnectionToGroup(UUID groupConnectionId, UUID cspConnectionId);
    void removeConnectionFromGroup(UUID groupConnectionId, UUID cspConnectionId);
}