package com.insurance.repository;

import com.insurance.entity.ClaimStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClaimStatusHistoryRepository extends JpaRepository<ClaimStatusHistory, Long> {

    List<ClaimStatusHistory> findByClaimIdOrderByUpdatedDateDesc(Long claimId);
}
