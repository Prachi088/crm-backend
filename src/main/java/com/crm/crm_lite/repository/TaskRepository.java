package com.crm.crm_lite.repository;

import com.crm.crm_lite.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("""
        SELECT t FROM Task t
        WHERE (:query = '' OR LOWER(t.title) LIKE LOWER(CONCAT('%', :query, '%'))
            OR LOWER(t.description) LIKE LOWER(CONCAT('%', :query, '%')))
        AND (:status = '' OR t.status = :status)
        AND (:priority = '' OR t.priority = :priority)
        AND (:completed IS NULL OR t.completed = :completed)
        """)
    Page<Task> search(@Param("query") String query,
                      @Param("status") String status,
                      @Param("priority") String priority,
                      @Param("completed") Boolean completed,
                      Pageable pageable);

    // Used by GET /api/tasks/upcoming — incomplete tasks with a due date,
    // soonest first. Tasks with a null dueDate are excluded since they
    // can't be meaningfully ordered "upcoming".
    @Query("""
        SELECT t FROM Task t
        WHERE t.completed = false
        AND t.dueDate IS NOT NULL
        ORDER BY t.dueDate ASC
        """)
    Page<Task> findUpcoming(Pageable pageable);

    // Used by CustomerService.delete() to detach a customer's tasks before deleting it,
    // avoiding the FK constraint violation ("is still referenced from table tasks").
    // Nulls the link rather than deleting the tasks themselves — the tasks are still
    // valid work items, they just no longer point at a (now-deleted) customer.
    @org.springframework.data.jpa.repository.Modifying
    @Query("UPDATE Task t SET t.relatedCustomer = NULL WHERE t.relatedCustomer.id = :customerId")
    void clearRelatedCustomer(@Param("customerId") Long customerId);
}