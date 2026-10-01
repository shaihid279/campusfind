package com.campusfind.controller.web;

import com.campusfind.dto.request.RatingRequest;
import com.campusfind.entity.Claim;
import com.campusfind.repository.ClaimRepository;
import com.campusfind.security.CustomUserPrincipal;
import com.campusfind.service.RatingService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RatingController {

    private final RatingService ratingService;
    private final ClaimRepository claimRepository;

    public RatingController(RatingService ratingService, ClaimRepository claimRepository) {
        this.ratingService = ratingService;
        this.claimRepository = claimRepository;
    }

    @GetMapping("/claims/{claimId}/rate")
    public String showRatingForm(@PathVariable Long claimId, Model model) {
        Claim claim = claimRepository.findById(claimId).orElseThrow();
        model.addAttribute("claim", claim);
        model.addAttribute("ratingRequest", new RatingRequest());
        return "claims/rate-finder";
    }

    @PostMapping("/claims/{claimId}/rate")
    public String submitRating(@PathVariable Long claimId,
                               @Valid @ModelAttribute("ratingRequest") RatingRequest request,
                               BindingResult result, Model model,
                               @AuthenticationPrincipal CustomUserPrincipal principal,
                               RedirectAttributes redirectAttributes) {
        Claim claim = claimRepository.findById(claimId).orElseThrow();
        if (result.hasErrors()) {
            model.addAttribute("claim", claim);
            return "claims/rate-finder";
        }
        try {
            ratingService.submitRating(claimId, request, principal.getUser());
        } catch (IllegalArgumentException e) {
            model.addAttribute("claim", claim);
            model.addAttribute("errorMessage", e.getMessage());
            return "claims/rate-finder";
        }
        redirectAttributes.addFlashAttribute("successMessage", "Thanks for rating! This helps recognize honest finders.");
        return "redirect:/claims/my";
    }

    @GetMapping("/hall-of-fame")
    public String hallOfFame(Model model) {
        model.addAttribute("honorRoll", ratingService.getHallOfFame());
        return "hall-of-fame";
    }
}