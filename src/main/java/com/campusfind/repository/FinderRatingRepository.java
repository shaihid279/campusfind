package com.campusfind.repository;

import com.campusfind.entity.FinderRating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FinderRatingRepository extends JpaRepository<FinderRating, Long> {
    List<FinderRating> findByFinderId(Long finderId);
    boolean existsByClaimId(Long claimId);
}