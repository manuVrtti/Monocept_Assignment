package com.insurance.repository;

import com.insurance.entity.Policy;
import com.insurance.entity.enums.PolicyStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PolicyRepository extends JpaRepository<Policy, Long> {

    Optional<Policy> findByPolicyNumber(String policyNumber);

    List<Policy> findByCustomerId(Long customerId);

    @Query("SELECT p FROM Policy p WHERE " +
           "(:policyStatus IS NULL OR p.policyStatus = :policyStatus) AND " +
           "(:customerId IS NULL OR p.customer.id = :customerId)")
    Page<Policy> findAllWithFilters(@Param("policyStatus") PolicyStatus policyStatus,
                                    @Param("customerId") Long customerId,
                                    Pageable pageable);

    @Query("SELECT p FROM Policy p WHERE p.customer.id = :customerId AND " +
           "(:policyStatus IS NULL OR p.policyStatus = :policyStatus)")
    Page<Policy> findByCustomerIdWithFilters(@Param("customerId") Long customerId,
                                              @Param("policyStatus") PolicyStatus policyStatus,
                                              Pageable pageable);
}
