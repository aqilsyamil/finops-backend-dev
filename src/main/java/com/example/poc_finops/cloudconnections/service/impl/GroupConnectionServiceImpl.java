package com.example.poc_finops.cloudconnections.service.impl;

import com.example.poc_finops.cloudconnections.domain.entity.GroupConnection;
import com.example.poc_finops.cloudconnections.domain.entity.GroupsConnections;
import com.example.poc_finops.cloudconnections.repository.CspConnectionRepository;
import com.example.poc_finops.cloudconnections.repository.GroupConnectionRepository;
import com.example.poc_finops.cloudconnections.repository.GroupsConnectionsRepository;
import com.example.poc_finops.cloudconnections.service.GroupConnectionService;
import com.example.poc_finops.organization.service.OrganizationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class GroupConnectionServiceImpl implements GroupConnectionService {

    private final GroupConnectionRepository groupConnectionRepository;
    private final GroupsConnectionsRepository groupsConnectionsRepository;
    private final CspConnectionRepository cspConnectionRepository;
    private final OrganizationService organizationService;

    @Override
    @Transactional(readOnly = true)
    public List<GroupConnection> getAllGroupConnections() {
        return groupConnectionRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<GroupConnection> getGroupConnectionsByOrganization(UUID organizationId) {
        return groupConnectionRepository.findByOrganizationId(organizationId);
    }

    @Override
    @Transactional(readOnly = true)
    public GroupConnection getGroupConnectionById(UUID id) {
        return groupConnectionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Group connection not found with id: " + id));
    }

    @Override
    public GroupConnection createGroupConnection(String name, UUID organizationId) {
        GroupConnection groupConnection = new GroupConnection();
        groupConnection.setName(name);
        groupConnection.setOrganization(organizationService.getOrganizationEntityById(organizationId));

        GroupConnection savedGroupConnection = groupConnectionRepository.save(groupConnection);
        log.info("Created group connection: {} for organization: {}", name, organizationId);
        return savedGroupConnection;
    }

    @Override
    public GroupConnection updateGroupConnection(UUID id, String name) {
        GroupConnection groupConnection = getGroupConnectionById(id);
        groupConnection.setName(name);

        GroupConnection updatedGroupConnection = groupConnectionRepository.save(groupConnection);
        log.info("Updated group connection: {}", id);
        return updatedGroupConnection;
    }

    @Override
    public void deleteGroupConnection(UUID id) {
        groupConnectionRepository.deleteById(id);
        log.info("Deleted group connection: {}", id);
    }

    @Override
    public void addConnectionToGroup(UUID groupConnectionId, UUID cspConnectionId) {
        GroupConnection groupConnection = getGroupConnectionById(groupConnectionId);
        var cspConnection = cspConnectionRepository.findById(cspConnectionId)
                .orElseThrow(() -> new RuntimeException("CSP connection not found with id: " + cspConnectionId));

        GroupsConnections groupsConnections = new GroupsConnections();
        groupsConnections.setGroupConnection(groupConnection);
        groupsConnections.setCspConnection(cspConnection);

        groupsConnectionsRepository.save(groupsConnections);
        log.info("Added CSP connection {} to group {}", cspConnectionId, groupConnectionId);
    }

    @Override
    public void removeConnectionFromGroup(UUID groupConnectionId, UUID cspConnectionId) {
        List<GroupsConnections> groupsConnections = groupsConnectionsRepository
                .findByGroupConnectionId(groupConnectionId);
        
        groupsConnections.stream()
                .filter(gc -> gc.getCspConnection().getId().equals(cspConnectionId))
                .findFirst()
                .ifPresent(gc -> {
                    groupsConnectionsRepository.delete(gc);
                    log.info("Removed CSP connection {} from group {}", cspConnectionId, groupConnectionId);
                });
    }
}