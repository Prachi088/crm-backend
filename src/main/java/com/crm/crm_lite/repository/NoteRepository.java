package com.crm.crm_lite.repository;

import com.crm.crm_lite.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByLeadId(Long leadId); // Get all notes for a specific lead
}