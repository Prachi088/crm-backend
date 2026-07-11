package com.crm.crm_lite.repository;

import com.crm.crm_lite.model.Contact;
import com.crm.crm_lite.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {

    Page<Contact> findByCustomer(Customer customer, Pageable pageable);

    @Query("""
        SELECT c FROM Contact c
        WHERE c.customer.id = :customerId
        AND (:query = '' OR LOWER(c.firstName) LIKE LOWER(CONCAT('%', :query, '%'))
            OR LOWER(c.lastName) LIKE LOWER(CONCAT('%', :query, '%'))
            OR LOWER(c.email) LIKE LOWER(CONCAT('%', :query, '%')))
        """)
    Page<Contact> searchByCustomer(@Param("customerId") Long customerId,
                                   @Param("query") String query,
                                   Pageable pageable);

    // Flat / cross-customer search — used by GET /api/contacts (no customerId scoping)
    @Query("""
        SELECT c FROM Contact c
        WHERE (:query = '' OR LOWER(c.firstName) LIKE LOWER(CONCAT('%', :query, '%'))
            OR LOWER(c.lastName) LIKE LOWER(CONCAT('%', :query, '%'))
            OR LOWER(c.email) LIKE LOWER(CONCAT('%', :query, '%')))
        """)
    Page<Contact> searchAll(@Param("query") String query, Pageable pageable);

    // Used by CustomerService.delete() to clear out a customer's contacts before
    // deleting the customer itself, avoiding the FK constraint violation
    // ("violates foreign key constraint ... is still referenced from table contacts").
    @Modifying
    @Query("DELETE FROM Contact c WHERE c.customer.id = :customerId")
    void deleteByCustomerId(@Param("customerId") Long customerId);
}