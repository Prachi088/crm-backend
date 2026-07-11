package com.crm.crm_lite.repository;

import com.crm.crm_lite.model.Lead;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeadRepository extends JpaRepository<Lead, Long> {

    boolean existsByEmailIgnoreCase(String email);

    // ── Used by LeadService.getAll() — no pagination (cached) ─────
    @Query("SELECT l FROM Lead l LEFT JOIN FETCH l.owner")
    List<Lead> findAllWithOwner();

    // ── Paginated + search — used by LeadService.search() ─────────
    @Query("""
        SELECT l FROM Lead l LEFT JOIN FETCH l.owner
        WHERE (
            LOWER(l.name)    LIKE LOWER(CONCAT('%', :q, '%')) OR
            LOWER(l.email)   LIKE LOWER(CONCAT('%', :q, '%')) OR
            LOWER(l.company) LIKE LOWER(CONCAT('%', :q, '%'))
        )
        AND (:status = 'ALL' OR l.status = :status)
        """)
    Page<Lead> search(
            @Param("q")      String q,
            @Param("status") String status,
            Pageable pageable);

    // count query for pagination (no fetch join needed)
    @Query("""
        SELECT COUNT(l) FROM Lead l
        WHERE (
            LOWER(l.name)    LIKE LOWER(CONCAT('%', :q, '%')) OR
            LOWER(l.email)   LIKE LOWER(CONCAT('%', :q, '%')) OR
            LOWER(l.company) LIKE LOWER(CONCAT('%', :q, '%'))
        )
        AND (:status = 'ALL' OR l.status = :status)
        """)
    long countSearch(
            @Param("q")      String q,
            @Param("status") String status);

    // ── Used by NoteService / LeadService ownership checks ────────
    @Query("SELECT l FROM Lead l LEFT JOIN FETCH l.owner WHERE l.id = :id")
    Optional<Lead> findByIdWithOwner(@Param("id") Long id);
}