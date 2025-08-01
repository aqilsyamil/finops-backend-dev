package com.example.poc_finops.organization.api;

import com.example.poc_finops.organization.api.dto.OrganizationDto;
import com.example.poc_finops.organization.service.OrganizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/organizations")
@RequiredArgsConstructor
@Tag(name = "Organization Management", description = "APIs for managing organizations")
public class OrganizationController {

    private final OrganizationService organizationService;

    @GetMapping
    @Operation(summary = "Get all organizations", description = "Retrieve a list of all organizations")
    public ResponseEntity<List<OrganizationDto>> getAllOrganizations() {
        List<OrganizationDto> organizations = organizationService.getAllOrganizations();
        return ResponseEntity.ok(organizations);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get organization by ID", description = "Retrieve an organization by its ID")
    public ResponseEntity<OrganizationDto> getOrganizationById(@PathVariable UUID id) {
        OrganizationDto organization = organizationService.getOrganizationById(id);
        return ResponseEntity.ok(organization);
    }

    @PostMapping
    @Operation(summary = "Create organization", description = "Create a new organization")
    public ResponseEntity<OrganizationDto> createOrganization(@RequestParam String organizationName) {
        OrganizationDto organization = organizationService.createOrganization(organizationName);
        return ResponseEntity.status(HttpStatus.CREATED).body(organization);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update organization", description = "Update an existing organization")
    public ResponseEntity<OrganizationDto> updateOrganization(@PathVariable UUID id, 
                                                             @RequestParam String organizationName) {
        OrganizationDto organization = organizationService.updateOrganization(id, organizationName);
        return ResponseEntity.ok(organization);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete organization", description = "Delete an organization by its ID")
    public ResponseEntity<Void> deleteOrganization(@PathVariable UUID id) {
        organizationService.deleteOrganization(id);
        return ResponseEntity.noContent().build();
    }
}