package com.example.poc_finops.budgetmanagement.domain.entity;

import com.example.poc_finops.cloudconnections.domain.entity.CspConnection;
import com.example.poc_finops.organization.domain.entity.Organization;
import com.example.poc_finops.organization.domain.entity.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "t_budgets", schema = "finops_v2")
@Data
@EqualsAndHashCode(callSuper = false)
public class Budgets {
    
    @Id
    @Column(name = "id")
    private UUID id = UUID.randomUUID();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "csp_connection_id", nullable = false)
    private CspConnection cspConnection;
    
    @Column(name = "name")
    private String name;
    
    @Column(name = "time_range")
    private String timeRange;
    
    @Column(name = "renewal_type")
    private String renewalType;
    
    @Column(name = "start_month")
    private LocalDate startMonth;
    
    @Column(name = "budget_type")
    private String budgetType;
    
    @Column(name = "amount")
    private Double amount;
    
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