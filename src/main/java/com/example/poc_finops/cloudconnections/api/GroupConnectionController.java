package com.example.poc_finops.cloudconnections.api;

import com.example.poc_finops.cloudconnections.api.dto.GroupConnectionDto;
import com.example.poc_finops.cloudconnections.domain.entity.GroupConnection;
import com.example.poc_finops.cloudconnections.service.GroupConnectionService;
import com.example.poc_finops.organization.api.dto.OrganizationDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/group-connections")
@RequiredArgsConstructor
@Tag(name = "Group Connection Management", description = "APIs for managing connection groups")
public class GroupConnectionController {

    private final GroupConnectionService groupConnectionService;

    @GetMapping
    @Operation(summary = "Get all group connections", description = "Retrieve a list of all connection groups")
    public ResponseEntity<List<GroupConnectionDto>> getAllGroupConnections() {
        List<GroupConnection> groupConnections = groupConnectionService.getAllGroupConnections();
        List<GroupConnectionDto> dtos = groupConnections.stream()
                .map(this::convertToDto)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/organization/{organizationId}")
    @Operation(summary = "Get group connections by organization", description = "Retrieve connection groups for a specific organization")
    public ResponseEntity<List<GroupConnectionDto>> getGroupConnectionsByOrganization(@PathVariable UUID organizationId) {
        List<GroupConnection> groupConnections = groupConnectionService.getGroupConnectionsByOrganization(organizationId);
        List<GroupConnectionDto> dtos = groupConnections.stream()
                .map(this::convertToDto)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get group connection by ID", description = "Retrieve a connection group by its ID")
    public ResponseEntity<GroupConnectionDto> getGroupConnectionById(@PathVariable UUID id) {
        GroupConnection groupConnection = groupConnectionService.getGroupConnectionById(id);
        GroupConnectionDto dto = convertToDto(groupConnection);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    @Operation(summary = "Create group connection", description = "Create a new connection group")
    public ResponseEntity<GroupConnectionDto> createGroupConnection(@RequestParam String name,
                                                                   @RequestParam UUID organizationId) {
        GroupConnection groupConnection = groupConnectionService.createGroupConnection(name, organizationId);
        GroupConnectionDto dto = convertToDto(groupConnection);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update group connection", description = "Update an existing connection group")
    public ResponseEntity<GroupConnectionDto> updateGroupConnection(@PathVariable UUID id,
                                                                   @RequestParam String name) {
        GroupConnection groupConnection = groupConnectionService.updateGroupConnection(id, name);
        GroupConnectionDto dto = convertToDto(groupConnection);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete group connection", description = "Delete a connection group by its ID")
    public ResponseEntity<Void> deleteGroupConnection(@PathVariable UUID id) {
        groupConnectionService.deleteGroupConnection(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{groupId}/connections/{connectionId}")
    @Operation(summary = "Add connection to group", description = "Add a CSP connection to a connection group")
    public ResponseEntity<Void> addConnectionToGroup(@PathVariable UUID groupId, @PathVariable UUID connectionId) {
        groupConnectionService.addConnectionToGroup(groupId, connectionId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{groupId}/connections/{connectionId}")
    @Operation(summary = "Remove connection from group", description = "Remove a CSP connection from a connection group")
    public ResponseEntity<Void> removeConnectionFromGroup(@PathVariable UUID groupId, @PathVariable UUID connectionId) {
        groupConnectionService.removeConnectionFromGroup(groupId, connectionId);
        return ResponseEntity.ok().build();
    }

    private GroupConnectionDto convertToDto(GroupConnection groupConnection) {
        GroupConnectionDto dto = new GroupConnectionDto();
        dto.setId(groupConnection.getId());
        dto.setName(groupConnection.getName());
        dto.setCreatedAt(groupConnection.getCreatedAt());
        dto.setUpdatedAt(groupConnection.getUpdatedAt());
        dto.setCreatedBy(groupConnection.getCreatedBy());
        dto.setUpdatedBy(groupConnection.getUpdatedBy());
        
        // Convert organization to DTO if it exists
        if (groupConnection.getOrganization() != null) {
            OrganizationDto orgDto = new OrganizationDto();
            orgDto.setId(groupConnection.getOrganization().getId());
            orgDto.setOrganizationName(groupConnection.getOrganization().getOrganizationName());
            orgDto.setCreatedAt(groupConnection.getOrganization().getCreatedAt());
            orgDto.setUpdatedAt(groupConnection.getOrganization().getUpdatedAt());
            dto.setOrganization(orgDto);
        }
        
        return dto;
    }
}