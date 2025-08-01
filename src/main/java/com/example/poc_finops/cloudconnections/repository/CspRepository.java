package com.example.poc_finops.cloudconnections.repository;

import com.example.poc_finops.cloudconnections.domain.entity.Csp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CspRepository extends JpaRepository<Csp, UUID> {
    Optional<Csp> findByName(String name);
    List<Csp> findByNameContainingIgnoreCase(String name);
}