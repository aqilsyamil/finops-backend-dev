package com.example.poc_finops.anomalydetection.domain.entity;

import com.example.poc_finops.organization.domain.entity.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "t_anomaly_alert_threshold", schema = "finops_v2")
@Data
@EqualsAndHashCode(callSuper = false)
public class AlertThreshold {
    
    @Id
    @Column(name = "id")
    private UUID id = UUID.randomUUID();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alert_id", nullable = false)
    private Alert alert;
    
    @Column(name = "threshold_amount_1")
    private Double thresholdAmount1;
    
    @Column(name = "threshold_amount_2")
    private Double thresholdAmount2;
    
    @Column(name = "threshold_type_1")
    private String thresholdType1;
    
    @Column(name = "threshold_type_2")
    private String thresholdType2;
    
    @Column(name = "relation")
    private String relation;
    
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