package com.example.poc_finops.cloudconnections.api;

import com.example.poc_finops.cloudconnections.api.dto.CreateConnectionRequest;
import com.example.poc_finops.cloudconnections.api.dto.CspConnectionDto;
import com.example.poc_finops.cloudconnections.service.ConnectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/connections")
@RequiredArgsConstructor
@Tag(name = "Connection Management", description = "APIs for managing CSP connections")
public class ConnectionController {

    private final ConnectionService connectionService;

    @GetMapping
    @Operation(summary = "Get all connections", description = "Retrieve a list of all CSP connections")
    public ResponseEntity<List<CspConnectionDto>> getAllConnections() {
        List<CspConnectionDto> connections = connectionService.getAllConnections();
        return ResponseEntity.ok(connections);
    }

    @GetMapping("/organization/{organizationId}")
    @Operation(summary = "Get connections by organization", description = "Retrieve connections for a specific organization")
    public ResponseEntity<List<CspConnectionDto>> getConnectionsByOrganization(@PathVariable UUID organizationId) {
        List<CspConnectionDto> connections = connectionService.getConnectionsByOrganization(organizationId);
        return ResponseEntity.ok(connections);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get connection by ID", description = "Retrieve a CSP connection by its ID")
    public ResponseEntity<CspConnectionDto> getConnectionById(@PathVariable UUID id) {
        CspConnectionDto connection = connectionService.getConnectionById(id);
        return ResponseEntity.ok(connection);
    }

    @PostMapping
    @Operation(summary = "Create connection", description = "Create a new CSP connection")
    public ResponseEntity<CspConnectionDto> createConnection(@RequestBody CreateConnectionRequest request) {
        CspConnectionDto connection = connectionService.createConnection(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(connection);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update connection", description = "Update an existing CSP connection")
    public ResponseEntity<CspConnectionDto> updateConnection(@PathVariable UUID id, 
                                                           @RequestBody CreateConnectionRequest request) {
        CspConnectionDto connection = connectionService.updateConnection(id, request);
        return ResponseEntity.ok(connection);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete connection", description = "Delete a CSP connection by its ID")
    public ResponseEntity<Void> deleteConnection(@PathVariable UUID id) {
        connectionService.deleteConnection(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/csp/{cspId}")
    @Operation(summary = "Get connections by CSP", description = "Retrieve connections for a specific CSP")
    public ResponseEntity<List<CspConnectionDto>> getConnectionsByCsp(@PathVariable UUID cspId) {
        List<CspConnectionDto> connections = connectionService.getConnectionsByCsp(cspId);
        return ResponseEntity.ok(connections);
    }

    @GetMapping("/datasource/{dataSourceId}")
    @Operation(summary = "Get connections by data source", description = "Retrieve connections for a specific data source")
    public ResponseEntity<List<CspConnectionDto>> getConnectionsByDataSource(@PathVariable UUID dataSourceId) {
        List<CspConnectionDto> connections = connectionService.getConnectionsByDataSource(dataSourceId);
        return ResponseEntity.ok(connections);
    }
    
    @PostMapping("/{id}/test")
    @Operation(summary = "Test connection", description = "Test and validate a CSP connection by attempting to connect with stored credentials")
    public ResponseEntity<CspConnectionDto> testConnection(@PathVariable UUID id) {
        CspConnectionDto connection = connectionService.testConnection(id);
        return ResponseEntity.ok(connection);
    }
}