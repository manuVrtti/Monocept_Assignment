package com.insurance.repository;

import com.insurance.entity.InsuranceProduct;
import com.insurance.entity.enums.ProductType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InsuranceProductRepository extends JpaRepository<InsuranceProduct, Long> {

    boolean existsByProductName(String productName);

    Optional<InsuranceProduct> findByProductName(String productName);

    @Query("SELECT p FROM InsuranceProduct p WHERE " +
           "(:productType IS NULL OR p.productType = :productType) AND " +
           "(:active IS NULL OR p.active = :active)")
    Page<InsuranceProduct> findAllWithFilters(@Param("productType") ProductType productType,
                                              @Param("active") Boolean active,
                                              Pageable pageable);
}
