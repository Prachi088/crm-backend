package com.crm.crm_lite.repository;

import com.crm.crm_lite.model.Lead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeadRepository extends JpaRepository<Lead, Long> {
    // FIX: case-insensitive check — prevents PRACHI@x.com vs prachi@x.com duplicates
    boolean existsByEmailIgnoreCase(String email);
}