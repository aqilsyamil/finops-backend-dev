package com.example.poc_finops.cloudconnections.repository;

import com.example.poc_finops.cloudconnections.domain.entity.Services;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ServicesRepository extends JpaRepository<Services, UUID> {
    
    List<Services> findByServiceName(String serviceName);
    
    List<Services> findByAccountId(String accountId);
    
    List<Services> findByResourceType(String resourceType);
    
    
    @Query("SELECT s FROM Services s WHERE s.serviceName = :serviceName AND s.accountId = :accountId")
    List<Services> findByServiceNameAndAccountId(@Param("serviceName") String serviceName, @Param("accountId") String accountId);
    
    @Query("SELECT s FROM Services s WHERE s.deletedAt IS NULL")
    List<Services> findAllActive();
}