package com.example.poc_finops.anomalydetection.domain.entity;

import com.example.poc_finops.organization.domain.entity.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "t_focus_anomaly_detection", schema = "finops_v2")
@Data
@EqualsAndHashCode(callSuper = false)
public class FocusAnomalyDetection {
    
    @Id
    @Column(name = "id")
    private UUID id = UUID.randomUUID();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "anomaly_monitor_id", nullable = false)
    private FocusAnomalyMonitors anomalyMonitor;
    
    @Column(name = "start_date")
    private LocalDate startDate;
    
    @Column(name = "last_detected_date")
    private LocalDate lastDetectedDate;
    
    @Column(name = "expected_spend")
    private Double expectedSpend;
    
    @Column(name = "actual_spend")
    private Double actualSpend;
    
    @Column(name = "linked_account_id")
    private String linkedAccountId;
    
    @Column(name = "linked_account_name")
    private String linkedAccountName;
    
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