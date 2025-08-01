package com.example.poc_finops.cloudconnections.service;

import com.example.poc_finops.cloudconnections.domain.entity.Csp;

import java.util.List;
import java.util.UUID;

public interface CspService {
    List<Csp> getAllCsps();
    Csp getCspById(UUID id);
    Csp createCsp(String name);
    Csp updateCsp(UUID id, String name);
    void deleteCsp(UUID id);
    Csp getCspByName(String name);
    List<Csp> searchCspsByName(String name);
}