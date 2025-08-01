package com.example.poc_finops.organization.repository;

import com.example.poc_finops.organization.domain.entity.RoleFeaturePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RoleFeaturePermissionRepository extends JpaRepository<RoleFeaturePermission, UUID> {
}