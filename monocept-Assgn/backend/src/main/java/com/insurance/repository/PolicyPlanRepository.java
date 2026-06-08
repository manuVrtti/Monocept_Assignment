package com.insurance.repository;

import com.insurance.entity.PolicyPlan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PolicyPlanRepository extends JpaRepository<PolicyPlan, Long> {

    List<PolicyPlan> findByInsuranceProductId(Long productId);

    List<PolicyPlan> findByInsuranceProductIdAndActiveTrue(Long productId);

    @Query("SELECT pp FROM PolicyPlan pp WHERE " +
           "(:productId IS NULL OR pp.insuranceProduct.id = :productId) AND " +
           "(:active IS NULL OR pp.active = :active)")
    Page<PolicyPlan> findAllWithFilters(@Param("productId") Long productId,
                                        @Param("active") Boolean active,
                                        Pageable pageable);
}
