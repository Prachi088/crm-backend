package com.crm.crm_lite.repository;

import com.crm.crm_lite.model.Lead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeadRepository extends JpaRepository<Lead, Long> {
    // JpaRepository gives you: findAll, findById, save, deleteById — all built-in
    boolean existsByEmail(String email); // Extra: check duplicate email
}