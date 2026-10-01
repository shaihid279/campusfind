package com.campusfind.repository;

import com.campusfind.entity.Item;
import com.campusfind.enums.ItemStatus;
import com.campusfind.enums.ItemType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Optional<Item> findByUniqueItemCode(String code);
    List<Item> findByItemTypeAndStatus(ItemType type, ItemStatus status);
    List<Item> findByUserId(Long userId);
    long countByItemType(ItemType type);
    long countByStatus(ItemStatus status);
}