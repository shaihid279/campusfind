package com.campusfind.service;

import com.campusfind.dto.request.ItemCreateRequest;
import com.campusfind.entity.Category;
import com.campusfind.entity.Item;
import com.campusfind.entity.User;
import com.campusfind.enums.ItemStatus;
import com.campusfind.enums.ItemType;
import com.campusfind.exception.ResourceNotFoundException;
import com.campusfind.repository.CategoryRepository;
import com.campusfind.repository.ItemRepository;
import com.campusfind.util.ItemCodeGenerator;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;
    private final ItemCodeGenerator itemCodeGenerator;
    private final MatchingService matchingService;

    public ItemService(ItemRepository itemRepository, CategoryRepository categoryRepository,
                       ItemCodeGenerator itemCodeGenerator, MatchingService matchingService) {
        this.itemRepository = itemRepository;
        this.categoryRepository = categoryRepository;
        this.itemCodeGenerator = itemCodeGenerator;
        this.matchingService = matchingService;
    }

    public Item createItem(ItemCreateRequest request, ItemType type, User reporter) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid category selected."));

        Item item = new Item();
        item.setUniqueItemCode(itemCodeGenerator.generate());
        item.setTitle(request.getTitle());
        item.setDescription(buildDescription(request));
        item.setItemType(type);
        item.setCategory(category);
        item.setLocation(request.getLocation());
        item.setItemDate(request.getItemDate());
        item.setItemTime(request.getItemTime());
        item.setColor(request.getColor());
        item.setBrand(request.getBrand());
        item.setIdentifyingFeatures(request.getIdentifyingFeatures());
        item.setStorageLocation(request.getStorageLocation());
        item.setStatus(ItemStatus.ACTIVE);
        item.setUser(reporter);

        Item saved = itemRepository.save(item);
        matchingService.findAndSaveMatches(saved);
        return saved;
    }

    private String buildDescription(ItemCreateRequest request) {
        StringBuilder sb = new StringBuilder();
        if (request.getDescription() != null) sb.append(request.getDescription());
        if (request.getAdditionalInformation() != null && !request.getAdditionalInformation().isBlank()) {
            sb.append("\n\nAdditional info: ").append(request.getAdditionalInformation());
        }
        return sb.toString();
    }

    public List<Item> getAllActiveItems() {
        return itemRepository.findAll().stream()
                .filter(i -> i.getStatus() != ItemStatus.CLOSED)
                .toList();
    }

    public List<Item> getItemsByType(ItemType type) {
        return itemRepository.findAll().stream()
                .filter(i -> i.getItemType() == type && i.getStatus() != ItemStatus.CLOSED)
                .toList();
    }

    public Item getByCode(String code) {
        return itemRepository.findByUniqueItemCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with code: " + code));
    }

    public List<Item> getMyItems(Long userId) {
        return itemRepository.findByUserId(userId);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
}