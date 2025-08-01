package com.example.poc_finops.costanalytics.api.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class UsageReportRequest {
    private UUID organizationId;
    private List<UUID> cspConnectionIds;
    private LocalDate startDate;
    private LocalDate endDate;
    private String groupBy; // service, region, resource, category
    private List<String> serviceNames;
    private List<String> regions;
    private List<String> chargeCategories;
    private List<String> resourceTypes;
    private String currency;
    private Boolean includeCredits = false;
    private Boolean includeTax = false;
    private Integer limit = 100;
    private String sortBy = "cost"; // cost, effectiveCost, usage
    private String sortOrder = "desc"; // asc, desc
}