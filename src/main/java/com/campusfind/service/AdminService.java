package com.campusfind.service;

import com.campusfind.dto.response.AdminStatsDto;
import com.campusfind.entity.Category;
import com.campusfind.entity.Item;
import com.campusfind.entity.User;
import com.campusfind.enums.ItemStatus;
import com.campusfind.enums.ItemType;
import com.campusfind.repository.CategoryRepository;
import com.campusfind.repository.ClaimRepository;
import com.campusfind.repository.ItemRepository;
import com.campusfind.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ClaimRepository claimRepository;
    private final CategoryRepository categoryRepository;

    public AdminService(UserRepository userRepository, ItemRepository itemRepository,
                        ClaimRepository claimRepository, CategoryRepository categoryRepository) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.claimRepository = claimRepository;
        this.categoryRepository = categoryRepository;
    }

    public AdminStatsDto getStats() {
        long totalUsers = userRepository.count();
        long lostReports = itemRepository.countByItemType(ItemType.LOST);
        long foundReports = itemRepository.countByItemType(ItemType.FOUND);
        long pendingClaims = claimRepository.findAll().stream()
                .filter(c -> c.getStatus().name().equals("PENDING") || c.getStatus().name().equals("UNDER_REVIEW"))
                .count();
        long returnedItems = itemRepository.countByStatus(ItemStatus.RETURNED);
        long totalItems = lostReports + foundReports;
        double recoveryRate = totalItems == 0 ? 0 : Math.round((returnedItems * 100.0 / totalItems) * 10) / 10.0;

        return new AdminStatsDto(totalUsers, lostReports, foundReports, pendingClaims, returnedItems, recoveryRate);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void toggleUserEnabled(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        user.setEnabled(!user.isEnabled());
        userRepository.save(user);
    }

    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    public void removeItem(Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow();
        item.setStatus(ItemStatus.CLOSED);
        itemRepository.save(item);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public void addCategory(String name) {
        if (categoryRepository.existsByName(name)) {
            throw new IllegalArgumentException("Category already exists.");
        }
        Category c = new Category();
        c.setName(name);
        categoryRepository.save(c);
    }

    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
}