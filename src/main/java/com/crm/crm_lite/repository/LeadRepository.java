package com.crm.crm_lite.repository;

import com.crm.crm_lite.model.Lead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeadRepository extends JpaRepository<Lead, Long> {
    boolean existsByEmailIgnoreCase(String email);

    @Query("SELECT l FROM Lead l LEFT JOIN FETCH l.owner")
    List<Lead> findAllWithOwner();
}