package com.insurance.repository;

import com.insurance.entity.PremiumPayment;
import com.insurance.entity.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PremiumPaymentRepository extends JpaRepository<PremiumPayment, Long> {

    boolean existsByTransactionReference(String transactionReference);

    List<PremiumPayment> findByPolicyId(Long policyId);

    List<PremiumPayment> findByPolicyIdOrderByPaymentDateDesc(Long policyId);

    @Query("SELECT pp FROM PremiumPayment pp WHERE " +
           "(:policyId IS NULL OR pp.policy.id = :policyId) AND " +
           "(:paymentStatus IS NULL OR pp.paymentStatus = :paymentStatus)")
    Page<PremiumPayment> findAllWithFilters(@Param("policyId") Long policyId,
                                             @Param("paymentStatus") PaymentStatus paymentStatus,
                                             Pageable pageable);

    @Query("SELECT pp FROM PremiumPayment pp WHERE " +
           "pp.policy.customer.id = :customerId AND " +
           "(:policyId IS NULL OR pp.policy.id = :policyId) AND " +
           "(:paymentStatus IS NULL OR pp.paymentStatus = :paymentStatus)")
    Page<PremiumPayment> findByCustomerIdWithFilters(@Param("customerId") Long customerId,
                                                      @Param("policyId") Long policyId,
                                                      @Param("paymentStatus") PaymentStatus paymentStatus,
                                                      Pageable pageable);
}
