package com.crm.crm_lite.repository;

import com.crm.crm_lite.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    // JOIN FETCH createdBy so Jackson can serialize it without a Hibernate session
    @Query("SELECT n FROM Note n LEFT JOIN FETCH n.createdBy WHERE n.lead.id = :leadId ORDER BY n.createdAt ASC")
    List<Note> findByLeadId(@Param("leadId") Long leadId);

    @Query("SELECT n FROM Note n LEFT JOIN FETCH n.createdBy WHERE n.customer.id = :customerId ORDER BY n.createdAt ASC")
    List<Note> findByCustomerId(@Param("customerId") Long customerId);

    // JOIN FETCH createdBy so ownership check works in update/delete
    @Query("SELECT n FROM Note n LEFT JOIN FETCH n.createdBy WHERE n.id = :id")
    Optional<Note> findByIdWithCreatedBy(@Param("id") Long id);
}