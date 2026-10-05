package com.campusfind.service;

import com.campusfind.dto.request.RatingRequest;
import com.campusfind.dto.response.HonorRollDto;
import com.campusfind.entity.Claim;
import com.campusfind.entity.FinderRating;
import com.campusfind.entity.User;
import com.campusfind.enums.ClaimStatus;
import com.campusfind.repository.ClaimRepository;
import com.campusfind.repository.FinderRatingRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RatingService {

    private final FinderRatingRepository finderRatingRepository;
    private final ClaimRepository claimRepository;

    public RatingService(FinderRatingRepository finderRatingRepository, ClaimRepository claimRepository) {
        this.finderRatingRepository = finderRatingRepository;
        this.claimRepository = claimRepository;
    }

    public void submitRating(Long claimId, RatingRequest request, User rater) {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new IllegalArgumentException("Claim not found."));

        if (claim.getStatus() != ClaimStatus.APPROVED) {
            throw new IllegalArgumentException("You can only rate the finder after your claim is approved.");
        }
        if (!claim.getClaimant().getId().equals(rater.getId())) {
            throw new IllegalArgumentException("You are not authorized to rate this claim.");
        }
        if (finderRatingRepository.existsByClaimId(claimId)) {
            throw new IllegalArgumentException("You have already rated this finder for this item.");
        }

        FinderRating fr = new FinderRating();
        fr.setClaim(claim);
        fr.setFinder(claim.getItem().getUser());
        fr.setRater(rater);
        fr.setRating(request.getRating());
        fr.setComment(request.getComment());
        finderRatingRepository.save(fr);
    }

    public boolean alreadyRated(Long claimId) {
        return finderRatingRepository.existsByClaimId(claimId);
    }

    /**
     * Builds the public "Hall of Fame" leaderboard of the most honest,
     * highest-rated students/staff who have returned found items.
     */
    public List<HonorRollDto> getHallOfFame() {
        List<FinderRating> all = finderRatingRepository.findAll();

        Map<User, List<FinderRating>> grouped = all.stream()
                .collect(Collectors.groupingBy(FinderRating::getFinder));

        return grouped.entrySet().stream()
                .map(e -> {
                    User finder = e.getKey();
                    List<FinderRating> ratings = e.getValue();
                    double avg = ratings.stream().mapToInt(FinderRating::getRating).average().orElse(0);
                    return new HonorRollDto(finder.getId(), finder.getFullName(), finder.getDepartment(),
                            finder.getProfilePhotoUrl(), Math.round(avg * 10) / 10.0, ratings.size());
                })
                .sorted(Comparator.comparingDouble(HonorRollDto::getAverageRating).reversed()
                        .thenComparing(Comparator.comparingLong(HonorRollDto::getTotalReturns).reversed()))
                .toList();
    }
}