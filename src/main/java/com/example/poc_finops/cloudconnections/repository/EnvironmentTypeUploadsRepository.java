package com.example.poc_finops.cloudconnections.repository;

import com.example.poc_finops.cloudconnections.domain.entity.EnvironmentTypeUploads;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EnvironmentTypeUploadsRepository extends JpaRepository<EnvironmentTypeUploads, UUID> {
    List<EnvironmentTypeUploads> findByCspConnectionId(UUID cspConnectionId);
    Optional<EnvironmentTypeUploads> findByFileName(String fileName);
}