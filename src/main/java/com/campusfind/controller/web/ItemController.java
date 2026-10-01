package com.campusfind.controller.web;

import com.campusfind.dto.request.ItemCreateRequest;
import com.campusfind.entity.Item;
import com.campusfind.enums.ItemType;
import com.campusfind.security.CustomUserPrincipal;
import com.campusfind.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public String listItems(@RequestParam(required = false) String type, Model model) {
        if ("lost".equalsIgnoreCase(type)) {
            model.addAttribute("items", itemService.getItemsByType(ItemType.LOST));
        } else if ("found".equalsIgnoreCase(type)) {
            model.addAttribute("items", itemService.getItemsByType(ItemType.FOUND));
        } else {
            model.addAttribute("items", itemService.getAllActiveItems());
        }
        return "items/list";
    }

    @GetMapping("/{code}")
    public String itemDetails(@PathVariable String code, Model model) {
        Item item = itemService.getByCode(code);
        model.addAttribute("item", item);
        return "items/details";
    }

    @GetMapping("/report-lost")
    public String showReportLostForm(Model model) {
        model.addAttribute("itemRequest", new ItemCreateRequest());
        model.addAttribute("categories", itemService.getAllCategories());
        return "items/report-lost";
    }

    @PostMapping("/report-lost")
    public String submitReportLost(@Valid @ModelAttribute("itemRequest") ItemCreateRequest request,
                                   BindingResult result, Model model,
                                   @AuthenticationPrincipal CustomUserPrincipal principal,
                                   RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("categories", itemService.getAllCategories());
            return "items/report-lost";
        }
        Item item = itemService.createItem(request, ItemType.LOST, principal.getUser());
        redirectAttributes.addFlashAttribute("successMessage",
                "Lost item reported successfully! Your item ID is " + item.getUniqueItemCode());
        return "redirect:/items";
    }

    @GetMapping("/report-found")
    public String showReportFoundForm(Model model) {
        model.addAttribute("itemRequest", new ItemCreateRequest());
        model.addAttribute("categories", itemService.getAllCategories());
        return "items/report-found";
    }

    @PostMapping("/report-found")
    public String submitReportFound(@Valid @ModelAttribute("itemRequest") ItemCreateRequest request,
                                    BindingResult result, Model model,
                                    @AuthenticationPrincipal CustomUserPrincipal principal,
                                    RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("categories", itemService.getAllCategories());
            return "items/report-found";
        }
        Item item = itemService.createItem(request, ItemType.FOUND, principal.getUser());
        redirectAttributes.addFlashAttribute("successMessage",
                "Found item reported successfully! Item ID is " + item.getUniqueItemCode());
        return "redirect:/items";
    }
}