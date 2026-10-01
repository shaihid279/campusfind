package com.campusfind.controller.api;

import com.campusfind.dto.response.MatchResultDto;
import com.campusfind.entity.Item;
import com.campusfind.entity.ItemMatch;
import com.campusfind.repository.ItemMatchRepository;
import com.campusfind.repository.ItemRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/items")
public class MatchApiController {

    private final ItemMatchRepository itemMatchRepository;
    private final ItemRepository itemRepository;

    public MatchApiController(ItemMatchRepository itemMatchRepository, ItemRepository itemRepository) {
        this.itemMatchRepository = itemMatchRepository;
        this.itemRepository = itemRepository;
    }

    @GetMapping("/{id}/matches")
    public List<MatchResultDto> getMatches(@PathVariable Long id) {
        Item item = itemRepository.findById(id).orElseThrow();
        List<ItemMatch> matches;

        if (item.getItemType().name().equals("LOST")) {
            matches = itemMatchRepository.findByLostItemId(id);
            return matches.stream().map(m -> new MatchResultDto(
                    m.getFoundItem().getId(), m.getFoundItem().getUniqueItemCode(),
                    m.getFoundItem().getTitle(), m.getFoundItem().getLocation(),
                    m.getMatchPercentage()
            )).toList();
        } else {
            matches = itemMatchRepository.findByFoundItemId(id);
            return matches.stream().map(m -> new MatchResultDto(
                    m.getLostItem().getId(), m.getLostItem().getUniqueItemCode(),
                    m.getLostItem().getTitle(), m.getLostItem().getLocation(),
                    m.getMatchPercentage()
            )).toList();
        }
    }
}