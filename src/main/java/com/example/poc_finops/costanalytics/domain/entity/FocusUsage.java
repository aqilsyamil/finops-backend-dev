package com.example.poc_finops.costanalytics.domain.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@Entity
@Table(name = "t_focus_report", schema = "finops_v2")
public class FocusUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "focus_log_id", nullable = false)
    private FocusLog focusLog;

    @Column(name = "availabilityzone")
    private String availabilityZone;

    @Column(name = "billedcost")
    private Double billedCost;

    @Column(name = "billingaccountid")
    private String billingAccountId;

    @Column(name = "billingaccountname")
    private String billingAccountName;

    @Column(name = "billingcurrency")
    private String billingCurrency;

    @Column(name = "billingperiodend")
    private OffsetDateTime billingPeriodEnd;

    @Column(name = "billingperiodstart")
    private OffsetDateTime billingPeriodStart;

    @Column(name = "chargecategory")
    private String chargeCategory;

    @Column(name = "chargeclass")
    private String chargeClass;

    @Column(name = "chargedescription")
    private String chargeDescription;

    @Column(name = "chargefrequency")
    private String chargeFrequency;

    @Column(name = "chargeperiodend")
    private OffsetDateTime chargePeriodEnd;

    @Column(name = "chargeperiodstart")
    private OffsetDateTime chargePeriodStart;

    @Column(name = "commitmentdiscountcategory")
    private String commitmentDiscountCategory;

    @Column(name = "commitmentdiscountid")
    private String commitmentDiscountId;

    @Column(name = "commitmentdiscountname")
    private String commitmentDiscountName;

    @Column(name = "commitmentdiscountstatus")
    private String commitmentDiscountStatus;

    @Column(name = "commitmentdiscounttype")
    private String commitmentDiscountType;

    @Column(name = "consumedquantity")
    private Double consumedQuantity;

    @Column(name = "consumedunit")
    private String consumedUnit;

    @Column(name = "contractedcost")
    private Double contractedCost;

    @Column(name = "contractedunitprice")
    private Double contractedUnitPrice;

    @Column(name = "effectivecost")
    private Double effectiveCost;

    @Column(name = "invoiceissuername")
    private String invoiceIssuerName;

    @Column(name = "listcost")
    private Double listCost;

    @Column(name = "listunitprice")
    private Double listUnitPrice;

    @Column(name = "pricingcategory")
    private String pricingCategory;

    @Column(name = "pricingquantity")
    private Double pricingQuantity;

    @Column(name = "pricingunit")
    private String pricingUnit;

    @Column(name = "providername")
    private String providerName;

    @Column(name = "publishername")
    private String publisherName;

    @Column(name = "regionid")
    private String regionId;

    @Column(name = "regionname")
    private String regionName;

    @Column(name = "resourceid")
    private String resourceId;

    @Column(name = "resourcename")
    private String resourceName;

    @Column(name = "resourcetype")
    private String resourceType;

    @Column(name = "servicecategory")
    private String serviceCategory;

    @Column(name = "servicename")
    private String serviceName;

    @Column(name = "skuid")
    private String skuId;

    @Column(name = "skupriceid")
    private String skuPriceId;

    @Column(name = "subaccountid")
    private String subAccountId;

    @Column(name = "subaccountname")
    private String subAccountName;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "tags")
    private Map<String, Object> tags;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "x_costcategories")
    private Map<String, Object> xCostCategories;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "x_discounts")
    private Map<String, Object> xDiscounts;

    @Column(name = "x_operation")
    private String xOperation;

    @Column(name = "x_servicecode")
    private String xServiceCode;

    @Column(name = "x_usagetype")
    private String xUsageType;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @Column(name = "created_by")
    private UUID createdBy;

    @Column(name = "updated_by")
    private UUID updatedBy;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;
}