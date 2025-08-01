package com.example.poc_finops.recommendations.domain.entity;

import com.example.poc_finops.costanalytics.domain.entity.FocusLog;
import com.example.poc_finops.organization.domain.entity.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "t_recommendations_report", schema = "finops_v2")
@Data
@EqualsAndHashCode(callSuper = false)
public class FocusRecommendations {
    
    @Id
    @Column(name = "id")
    private UUID id = UUID.randomUUID();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "focus_log_id", nullable = false)
    private FocusLog focusLog;
    
    @Column(name = "account_id")
    private String accountId;
    
    @Column(name = "action_type")
    private String actionType;
    
    @Column(name = "currency_code")
    private String currencyCode;
    
    @Column(name = "current_resource_details")
    private String currentResourceDetails;
    
    @Column(name = "current_resource_summary")
    private String currentResourceSummary;
    
    @Column(name = "current_resource_type")
    private String currentResourceType;
    
    @Column(name = "estimated_monthly_cost_after_discount")
    private Double estimatedMonthlyCostAfterDiscount;
    
    @Column(name = "estimated_monthly_cost_before_discount")
    private Double estimatedMonthlyCostBeforeDiscount;
    
    @Column(name = "estimated_monthly_savings_after_discount")
    private Double estimatedMonthlySavingsAfterDiscount;
    
    @Column(name = "estimated_savings_percentage_after_discount")
    private Double estimatedSavingsPercentageAfterDiscount;
    
    @Column(name = "estimated_savings_percentage_before_discount")
    private Double estimatedSavingsPercentageBeforeDiscount;
    
    @Column(name = "estimated_monthly_savings_before_discount")
    private Double estimatedMonthlySavingsBeforeDiscount;
    
    @Column(name = "implementation_effort")
    private String implementationEffort;
    
    @Column(name = "last_refresh_timestamp")
    private OffsetDateTime lastRefreshTimestamp;
    
    @Column(name = "recommendation_id")
    private String recommendationId;
    
    @Column(name = "recommendation_lookback_period_in_days")
    private Integer recommendationLookbackPeriodInDays;
    
    @Column(name = "recommendation_source")
    private String recommendationSource;
    
    @Column(name = "recommended_resource_details")
    private String recommendedResourceDetails;
    
    @Column(name = "recommended_resource_summary")
    private String recommendedResourceSummary;
    
    @Column(name = "recommended_resource_type")
    private String recommendedResourceType;
    
    @Column(name = "region")
    private String region;
    
    @Column(name = "resource_arn")
    private String resourceArn;
    
    @Column(name = "restart_needed")
    private Boolean restartNeeded;
    
    @Column(name = "rollback_possible")
    private Boolean rollbackPossible;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "tags")
    private Map<String, Object> tags;
    
    @Column(name = "created_at")
    private OffsetDateTime createdAt;
    
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    private User updatedBy;
    
    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
        if (id == null) {
            id = UUID.randomUUID();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}