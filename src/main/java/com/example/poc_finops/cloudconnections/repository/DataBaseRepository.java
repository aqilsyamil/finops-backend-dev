package com.example.poc_finops.cloudconnections.repository;

import com.example.poc_finops.cloudconnections.domain.entity.DataBase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DataBaseRepository extends JpaRepository<DataBase, UUID> {
    List<DataBase> findByCspConnectionId(UUID cspConnectionId);
    Optional<DataBase> findByName(String name);
}