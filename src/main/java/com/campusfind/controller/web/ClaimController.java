package com.campusfind.controller.web;

import com.campusfind.dto.request.ClaimRequest;
import com.campusfind.entity.Item;
import com.campusfind.repository.ItemRepository;
import com.campusfind.security.CustomUserPrincipal;
import com.campusfind.service.ClaimService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/claims")
public class ClaimController {

    private final ClaimService claimService;
    private final ItemRepository itemRepository;

    public ClaimController(ClaimService claimService, ItemRepository itemRepository) {
        this.claimService = claimService;
        this.itemRepository = itemRepository;
    }

    @GetMapping("/{itemId}/submit")
    public String showClaimForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId).orElseThrow();
        model.addAttribute("item", item);
        model.addAttribute("claimRequest", new ClaimRequest());
        return "claims/submit";
    }

    @PostMapping("/{itemId}/submit")
    public String processClaim(@PathVariable Long itemId,
                               @Valid @ModelAttribute("claimRequest") ClaimRequest request,
                               BindingResult result, Model model,
                               @AuthenticationPrincipal CustomUserPrincipal principal,
                               RedirectAttributes redirectAttributes) {
        Item item = itemRepository.findById(itemId).orElseThrow();
        if (result.hasErrors()) {
            model.addAttribute("item", item);
            return "claims/submit";
        }
        try {
            claimService.submitClaim(itemId, request, principal.getUser());
        } catch (IllegalArgumentException e) {
            model.addAttribute("item", item);
            model.addAttribute("errorMessage", e.getMessage());
            return "claims/submit";
        }
        redirectAttributes.addFlashAttribute("successMessage", "Claim submitted successfully! It is now pending review.");
        return "redirect:/claims/my";
    }

    @GetMapping("/my")
    public String myClaims(Model model, @AuthenticationPrincipal CustomUserPrincipal principal) {
        model.addAttribute("claims", claimService.getMyClaims(principal.getId()));
        return "claims/my-claims";
    }
}