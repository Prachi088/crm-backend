package com.crm.crm_lite.repository;

import com.crm.crm_lite.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Query("""
        SELECT c FROM Customer c
        WHERE (:query = '' OR LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%'))
            OR LOWER(c.company) LIKE LOWER(CONCAT('%', :query, '%'))
            OR LOWER(c.email) LIKE LOWER(CONCAT('%', :query, '%')))
        AND (:status = '' OR c.status = :status)
        AND (:industry = '' OR c.industry = :industry)
        """)
    Page<Customer> search(@Param("query") String query,
                          @Param("status") String status,
                          @Param("industry") String industry,
                          Pageable pageable);
}
