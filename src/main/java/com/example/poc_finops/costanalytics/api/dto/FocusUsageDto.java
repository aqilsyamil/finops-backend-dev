package com.example.poc_finops.costanalytics.api.dto;

import lombok.Data;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Data
public class FocusUsageDto {
    private UUID id;
    private UUID focusLogId;
    private String availabilityZone;
    private Double billedCost;
    private String billingAccountId;
    private String billingAccountName;
    private String billingCurrency;
    private OffsetDateTime billingPeriodEnd;
    private OffsetDateTime billingPeriodStart;
    private String chargeCategory;
    private String chargeClass;
    private String chargeDescription;
    private String chargeFrequency;
    private OffsetDateTime chargePeriodEnd;
    private OffsetDateTime chargePeriodStart;
    private String commitmentDiscountCategory;
    private String commitmentDiscountId;
    private String commitmentDiscountName;
    private String commitmentDiscountStatus;
    private String commitmentDiscountType;
    private Double consumedQuantity;
    private String consumedUnit;
    private Double contractedCost;
    private Double contractedUnitPrice;
    private Double effectiveCost;
    private String invoiceIssuerName;
    private Double listCost;
    private Double listUnitPrice;
    private String pricingCategory;
    private Double pricingQuantity;
    private String pricingUnit;
    private String providerName;
    private String publisherName;
    private String regionId;
    private String regionName;
    private String resourceId;
    private String resourceName;
    private String resourceType;
    private String serviceCategory;
    private String serviceName;
    private String skuId;
    private String skuPriceId;
    private String subAccountId;
    private String subAccountName;
    private Map<String, Object> tags;
    private Map<String, Object> xCostCategories;
    private Map<String, Object> xDiscounts;
    private String xOperation;
    private String xServiceCode;
    private String xUsageType;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private UUID createdBy;
    private UUID updatedBy;
}