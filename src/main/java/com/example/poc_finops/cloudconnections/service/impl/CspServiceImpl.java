package com.example.poc_finops.cloudconnections.service.impl;

import com.example.poc_finops.cloudconnections.domain.entity.Csp;
import com.example.poc_finops.cloudconnections.repository.CspRepository;
import com.example.poc_finops.cloudconnections.service.CspService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CspServiceImpl implements CspService {

    private final CspRepository cspRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Csp> getAllCsps() {
        return cspRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Csp getCspById(UUID id) {
        return cspRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("CSP not found with id: " + id));
    }

    @Override
    public Csp createCsp(String name) {
        Csp csp = new Csp();
        csp.setName(name);
        
        Csp savedCsp = cspRepository.save(csp);
        log.info("Created CSP: {}", name);
        return savedCsp;
    }

    @Override
    public Csp updateCsp(UUID id, String name) {
        Csp csp = getCspById(id);
        csp.setName(name);
        
        Csp updatedCsp = cspRepository.save(csp);
        log.info("Updated CSP: {}", id);
        return updatedCsp;
    }

    @Override
    public void deleteCsp(UUID id) {
        cspRepository.deleteById(id);
        log.info("Deleted CSP: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Csp getCspByName(String name) {
        return cspRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("CSP not found with name: " + name));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Csp> searchCspsByName(String name) {
        return cspRepository.findByNameContainingIgnoreCase(name);
    }
}