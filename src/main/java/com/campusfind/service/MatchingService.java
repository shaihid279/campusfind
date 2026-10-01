package com.campusfind.service;

import com.campusfind.entity.Item;
import com.campusfind.entity.ItemMatch;
import com.campusfind.entity.Notification;
import com.campusfind.enums.ItemType;
import com.campusfind.enums.NotificationType;
import com.campusfind.repository.ItemMatchRepository;
import com.campusfind.repository.ItemRepository;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class MatchingService {

    private static final int MATCH_THRESHOLD = 60;

    private final ItemRepository itemRepository;
    private final ItemMatchRepository itemMatchRepository;
    private final NotificationService notificationService;

    public MatchingService(ItemRepository itemRepository, ItemMatchRepository itemMatchRepository,
                           NotificationService notificationService) {
        this.itemRepository = itemRepository;
        this.itemMatchRepository = itemMatchRepository;
        this.notificationService = notificationService;
    }

    /**
     * Called right after a new item is created. Scans the opposite type
     * (LOST scans FOUND items, and vice versa) for possible matches.
     */
    public void findAndSaveMatches(Item newItem) {
        ItemType oppositeType = newItem.getItemType() == ItemType.LOST ? ItemType.FOUND : ItemType.LOST;
        List<Item> candidates = itemRepository.findByItemTypeAndStatus(oppositeType,
                com.campusfind.enums.ItemStatus.ACTIVE);

        for (Item candidate : candidates) {
            int score = calculateMatchPercentage(newItem, candidate);
            if (score >= MATCH_THRESHOLD) {
                saveMatch(newItem, candidate, score);
            }
        }
    }

    private void saveMatch(Item newItem, Item candidate, int score) {
        Item lostItem = newItem.getItemType() == ItemType.LOST ? newItem : candidate;
        Item foundItem = newItem.getItemType() == ItemType.FOUND ? newItem : candidate;

        ItemMatch match = new ItemMatch();
        match.setLostItem(lostItem);
        match.setFoundItem(foundItem);
        match.setMatchPercentage(score);
        itemMatchRepository.save(match);

        String message = "Possible match found (" + score + "%) for your " +
                (newItem.getItemType() == ItemType.LOST ? "lost" : "found") +
                " item: " + newItem.getTitle();

        notificationService.createNotification(lostItem.getUser(), message, NotificationType.MATCH);
        notificationService.createNotification(foundItem.getUser(), message, NotificationType.MATCH);
    }

    /**
     * Rule-based weighted scoring. Fully transparent, no AI claims.
     * Category: 25%, Title keywords: 20%, Brand: 15%, Color: 15%,
     * Location: 15%, Date proximity (within 7 days): 10%
     */
    public int calculateMatchPercentage(Item a, Item b) {
        double score = 0;

        // Category match - 25%
        if (a.getCategory() != null && b.getCategory() != null
                && a.getCategory().getId().equals(b.getCategory().getId())) {
            score += 25;
        }

        // Title keyword overlap - 20%
        if (hasKeywordOverlap(a.getTitle(), b.getTitle())) {
            score += 20;
        }

        // Brand match - 15%
        if (isNonEmptyEqualIgnoreCase(a.getBrand(), b.getBrand())) {
            score += 15;
        }

        // Color match - 15%
        if (isNonEmptyEqualIgnoreCase(a.getColor(), b.getColor())) {
            score += 15;
        }

        // Location similarity - 15%
        if (hasKeywordOverlap(a.getLocation(), b.getLocation())) {
            score += 15;
        }

        // Date proximity within 7 days - 10%
        if (a.getItemDate() != null && b.getItemDate() != null) {
            long daysBetween = Math.abs(ChronoUnit.DAYS.between(a.getItemDate(), b.getItemDate()));
            if (daysBetween <= 7) {
                score += 10;
            }
        }

        return (int) Math.round(score);
    }

    private boolean isNonEmptyEqualIgnoreCase(String a, String b) {
        if (a == null || b == null || a.isBlank() || b.isBlank()) return false;
        return a.trim().equalsIgnoreCase(b.trim());
    }

    private boolean hasKeywordOverlap(String a, String b) {
        if (a == null || b == null) return false;
        String[] wordsA = a.toLowerCase().split("\\s+");
        String[] wordsB = b.toLowerCase().split("\\s+");
        for (String wa : wordsA) {
            if (wa.length() < 3) continue;
            for (String wb : wordsB) {
                if (wa.equals(wb)) return true;
            }
        }
        return false;
    }
}