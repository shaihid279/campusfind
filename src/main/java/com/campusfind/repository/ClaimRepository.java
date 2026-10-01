package com.campusfind.repository;

import com.campusfind.entity.Claim;
import com.campusfind.enums.ClaimStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClaimRepository extends JpaRepository<Claim, Long> {
    List<Claim> findByClaimantId(Long claimantId);
    List<Claim> findByItemId(Long itemId);
    List<Claim> findByItemIdAndStatus(Long itemId, ClaimStatus status);
}