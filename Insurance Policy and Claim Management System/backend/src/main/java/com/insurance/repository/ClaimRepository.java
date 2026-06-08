package com.insurance.repository;

import com.insurance.entity.Claim;
import com.insurance.entity.enums.ClaimStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, Long> {

    @Query("SELECT c FROM Claim c WHERE " +
           "(:claimStatus IS NULL OR c.claimStatus = :claimStatus) AND " +
           "(:customerId IS NULL OR c.policy.customer.id = :customerId)")
    Page<Claim> findAllWithFilters(@Param("claimStatus") ClaimStatus claimStatus,
                                    @Param("customerId") Long customerId,
                                    Pageable pageable);

    @Query("SELECT c FROM Claim c WHERE c.policy.customer.id = :customerId AND " +
           "(:claimStatus IS NULL OR c.claimStatus = :claimStatus)")
    Page<Claim> findByCustomerIdWithFilters(@Param("customerId") Long customerId,
                                             @Param("claimStatus") ClaimStatus claimStatus,
                                             Pageable pageable);
}
