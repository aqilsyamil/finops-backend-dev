package com.example.poc_finops.budgetmanagement.api;

import com.example.poc_finops.budgetmanagement.api.dto.BudgetDto;
import com.example.poc_finops.budgetmanagement.api.dto.CreateBudgetRequest;
import com.example.poc_finops.budgetmanagement.service.BudgetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
@Tag(name = "Budget Management", description = "APIs for managing budgets")
public class BudgetController {

    private final BudgetService budgetService;

    @GetMapping
    @Operation(summary = "Get all budgets", description = "Retrieve a list of all budgets")
    public ResponseEntity<List<BudgetDto>> getAllBudgets() {
        List<BudgetDto> budgets = budgetService.getAllBudgets();
        return ResponseEntity.ok(budgets);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get budget by ID", description = "Retrieve a budget by its ID")
    public ResponseEntity<BudgetDto> getBudgetById(@PathVariable UUID id) {
        BudgetDto budget = budgetService.getBudgetById(id);
        return ResponseEntity.ok(budget);
    }

    @GetMapping("/organization/{organizationId}")
    @Operation(summary = "Get budgets by organization", description = "Retrieve budgets for a specific organization")
    public ResponseEntity<List<BudgetDto>> getBudgetsByOrganization(@PathVariable UUID organizationId) {
        List<BudgetDto> budgets = budgetService.getBudgetsByOrganization(organizationId);
        return ResponseEntity.ok(budgets);
    }

    @GetMapping("/organization/{organizationId}/connection/{cspConnectionId}")
    @Operation(summary = "Get budgets by organization and connection", description = "Retrieve budgets for a specific organization and CSP connection")
    public ResponseEntity<List<BudgetDto>> getBudgetsByOrganizationAndConnection(
            @PathVariable UUID organizationId,
            @PathVariable UUID cspConnectionId) {
        List<BudgetDto> budgets = budgetService.getBudgetsByOrganizationAndConnection(organizationId, cspConnectionId);
        return ResponseEntity.ok(budgets);
    }

    @PostMapping
    @Operation(summary = "Create budget", description = "Create a new budget")
    public ResponseEntity<BudgetDto> createBudget(@Valid @RequestBody CreateBudgetRequest request) {
        BudgetDto budget = budgetService.createBudget(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(budget);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update budget", description = "Update an existing budget")
    public ResponseEntity<BudgetDto> updateBudget(@PathVariable UUID id, 
                                                 @Valid @RequestBody CreateBudgetRequest request) {
        BudgetDto budget = budgetService.updateBudget(id, request);
        return ResponseEntity.ok(budget);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete budget", description = "Delete a budget by its ID")
    public ResponseEntity<Void> deleteBudget(@PathVariable UUID id) {
        budgetService.deleteBudget(id);
        return ResponseEntity.noContent().build();
    }
}