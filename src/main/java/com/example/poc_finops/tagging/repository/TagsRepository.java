package com.example.poc_finops.tagging.repository;

import com.example.poc_finops.tagging.domain.entity.Tags;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TagsRepository extends JpaRepository<Tags, UUID> {
    
    List<Tags> findByCspConnectionId(UUID cspConnectionId);
    
    List<Tags> findByServiceId(UUID serviceId);
    
    @Query("SELECT t FROM Tags t WHERE t.service.serviceName = :serviceName")
    List<Tags> findByServiceName(@Param("serviceName") String serviceName);
    
    @Query("SELECT t FROM Tags t WHERE t.cspConnection.id = :cspConnectionId AND t.service.id = :serviceId")
    List<Tags> findByCspConnectionIdAndServiceId(@Param("cspConnectionId") UUID cspConnectionId, 
                                                 @Param("serviceId") UUID serviceId);
    
    @Query("SELECT t FROM Tags t WHERE t.cspConnection.id = :cspConnectionId AND t.service.serviceName = :serviceName")
    List<Tags> findByCspConnectionIdAndServiceName(@Param("cspConnectionId") UUID cspConnectionId, 
                                                   @Param("serviceName") String serviceName);
}