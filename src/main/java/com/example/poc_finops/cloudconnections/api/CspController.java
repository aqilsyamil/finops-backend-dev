package com.example.poc_finops.cloudconnections.api;

import com.example.poc_finops.cloudconnections.domain.entity.Csp;
import com.example.poc_finops.cloudconnections.service.CspService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/csps")
@RequiredArgsConstructor
@Tag(name = "CSP Management", description = "APIs for managing Cloud Service Providers")
public class CspController {

    private final CspService cspService;

    @GetMapping
    @Operation(summary = "Get all CSPs", description = "Retrieve a list of all cloud service providers")
    public ResponseEntity<List<Csp>> getAllCsps() {
        List<Csp> csps = cspService.getAllCsps();
        return ResponseEntity.ok(csps);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get CSP by ID", description = "Retrieve a cloud service provider by its ID")
    public ResponseEntity<Csp> getCspById(@PathVariable UUID id) {
        Csp csp = cspService.getCspById(id);
        return ResponseEntity.ok(csp);
    }

    @PostMapping
    @Operation(summary = "Create CSP", description = "Create a new cloud service provider")
    public ResponseEntity<Csp> createCsp(@RequestParam String name) {
        Csp csp = cspService.createCsp(name);
        return ResponseEntity.status(HttpStatus.CREATED).body(csp);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update CSP", description = "Update an existing cloud service provider")
    public ResponseEntity<Csp> updateCsp(@PathVariable UUID id, @RequestParam String name) {
        Csp csp = cspService.updateCsp(id, name);
        return ResponseEntity.ok(csp);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete CSP", description = "Delete a cloud service provider by its ID")
    public ResponseEntity<Void> deleteCsp(@PathVariable UUID id) {
        cspService.deleteCsp(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Search CSPs by name", description = "Search cloud service providers by name")
    public ResponseEntity<List<Csp>> searchCspsByName(@RequestParam String name) {
        List<Csp> csps = cspService.searchCspsByName(name);
        return ResponseEntity.ok(csps);
    }
}