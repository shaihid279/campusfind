package com.campusfind.service;

import com.campusfind.dto.request.ClaimRequest;
import com.campusfind.entity.Claim;
import com.campusfind.entity.Item;
import com.campusfind.entity.User;
import com.campusfind.enums.ClaimStatus;
import com.campusfind.enums.ItemStatus;
import com.campusfind.enums.NotificationType;
import com.campusfind.repository.ClaimRepository;
import com.campusfind.repository.ItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClaimService {

    private final ClaimRepository claimRepository;
    private final ItemRepository itemRepository;
    private final NotificationService notificationService;
    private final ActivityLogService activityLogService;

    public ClaimService(ClaimRepository claimRepository, ItemRepository itemRepository,
                        NotificationService notificationService, ActivityLogService activityLogService) {
        this.claimRepository = claimRepository;
        this.itemRepository = itemRepository;
        this.notificationService = notificationService;
        this.activityLogService = activityLogService;
    }

    public Claim submitClaim(Long itemId, ClaimRequest request, User claimant) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found."));

        if (item.getUser().getId().equals(claimant.getId())) {
            throw new IllegalArgumentException("You cannot claim an item you reported yourself.");
        }

        boolean alreadyPending = claimRepository.findByItemIdAndStatus(itemId, ClaimStatus.PENDING).stream()
                .anyMatch(c -> c.getClaimant().getId().equals(claimant.getId()));
        if (alreadyPending) {
            throw new IllegalArgumentException("You already have a pending claim for this item.");
        }

        StringBuilder proof = new StringBuilder();
        proof.append("Unique feature: ").append(request.getUniqueFeature());
        if (request.getApproximatePurchaseDate() != null && !request.getApproximatePurchaseDate().isBlank())
            proof.append("\nApprox. purchase date: ").append(request.getApproximatePurchaseDate());
        if (request.getPreviousLocation() != null && !request.getPreviousLocation().isBlank())
            proof.append("\nPrevious location: ").append(request.getPreviousLocation());
        if (request.getSerialNumber() != null && !request.getSerialNumber().isBlank())
            proof.append("\nSerial number: ").append(request.getSerialNumber());

        Claim claim = new Claim();
        claim.setItem(item);
        claim.setClaimant(claimant);
        claim.setProofDescription(proof.toString());
        claim.setStatus(ClaimStatus.PENDING);
        Claim saved = claimRepository.save(claim);

        notificationService.createNotification(item.getUser(),
                "A claim has been submitted for your item: " + item.getTitle(), NotificationType.CLAIM_UPDATE);
        notificationService.createNotification(claimant,
                "Your claim for \"" + item.getTitle() + "\" is pending review.", NotificationType.CLAIM_UPDATE);

        activityLogService.log(claimant, "CLAIM_SUBMITTED", "Claim submitted for item " + item.getUniqueItemCode());

        return saved;
    }

    public List<Claim> getMyClaims(Long claimantId) {
        return claimRepository.findByClaimantId(claimantId);
    }

    public List<Claim> getAllPendingClaims() {
        return claimRepository.findAll().stream()
                .filter(c -> c.getStatus() == ClaimStatus.PENDING || c.getStatus() == ClaimStatus.UNDER_REVIEW)
                .toList();
    }

    public void moveToUnderReview(Long claimId) {
        Claim claim = getClaim(claimId);
        claim.setStatus(ClaimStatus.UNDER_REVIEW);
        claimRepository.save(claim);
    }

    public void approveClaim(Long claimId, String adminComment, User reviewer) {
        Claim claim = getClaim(claimId);
        claim.setStatus(ClaimStatus.APPROVED);
        claim.setAdminComment(adminComment);
        claimRepository.save(claim);

        Item item = claim.getItem();
        item.setStatus(ItemStatus.RETURNED);
        itemRepository.save(item);

        // Auto-reject other pending claims for the same item
        claimRepository.findByItemId(item.getId()).stream()
                .filter(c -> !c.getId().equals(claimId) &&
                        (c.getStatus() == ClaimStatus.PENDING || c.getStatus() == ClaimStatus.UNDER_REVIEW))
                .forEach(c -> {
                    c.setStatus(ClaimStatus.REJECTED);
                    c.setAdminComment("Item already returned to another verified claimant.");
                    claimRepository.save(c);
                    notificationService.createNotification(c.getClaimant(),
                            "Your claim for \"" + item.getTitle() + "\" was rejected.", NotificationType.CLAIM_UPDATE);
                });

        notificationService.createNotification(claim.getClaimant(),
                "Your claim has been approved! Item: " + item.getTitle(), NotificationType.CLAIM_UPDATE);
        notificationService.createNotification(item.getUser(),
                "Your item has been marked as recovered: " + item.getTitle(), NotificationType.RECOVERED);

        activityLogService.log(reviewer, "CLAIM_APPROVED", "Claim approved for item " + item.getUniqueItemCode());
    }

    public void rejectClaim(Long claimId, String adminComment, User reviewer) {
        Claim claim = getClaim(claimId);
        claim.setStatus(ClaimStatus.REJECTED);
        claim.setAdminComment(adminComment);
        claimRepository.save(claim);

        notificationService.createNotification(claim.getClaimant(),
                "Your claim was rejected: " + adminComment, NotificationType.CLAIM_UPDATE);

        activityLogService.log(reviewer, "CLAIM_REJECTED", "Claim rejected for item " + claim.getItem().getUniqueItemCode());
    }

    private Claim getClaim(Long claimId) {
        return claimRepository.findById(claimId)
                .orElseThrow(() -> new IllegalArgumentException("Claim not found."));
    }
}