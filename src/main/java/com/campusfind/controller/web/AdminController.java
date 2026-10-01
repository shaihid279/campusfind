package com.campusfind.controller.web;

import com.campusfind.security.CustomUserPrincipal;
import com.campusfind.service.ActivityLogService;
import com.campusfind.service.AdminService;
import com.campusfind.service.ClaimService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final ClaimService claimService;
    private final ActivityLogService activityLogService;

    public AdminController(AdminService adminService, ClaimService claimService,
                           ActivityLogService activityLogService) {
        this.adminService = adminService;
        this.claimService = claimService;
        this.activityLogService = activityLogService;
    }

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("stats", adminService.getStats());
        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("users", adminService.getAllUsers());
        return "admin/users";
    }

    @PostMapping("/users/{id}/toggle")
    public String toggleUser(@PathVariable Long id) {
        adminService.toggleUserEnabled(id);
        return "redirect:/admin/users";
    }

    @GetMapping("/items")
    public String items(Model model) {
        model.addAttribute("items", adminService.getAllItems());
        return "admin/items";
    }

    @PostMapping("/items/{id}/remove")
    public String removeItem(@PathVariable Long id) {
        adminService.removeItem(id);
        return "redirect:/admin/items";
    }

    @GetMapping("/claims")
    public String claims(Model model) {
        model.addAttribute("claims", claimService.getAllPendingClaims());
        return "admin/claims";
    }

    @PostMapping("/claims/{id}/under-review")
    public String moveUnderReview(@PathVariable Long id) {
        claimService.moveToUnderReview(id);
        return "redirect:/admin/claims";
    }

    @PostMapping("/claims/{id}/approve")
    public String approveClaim(@PathVariable Long id,
                               @RequestParam(required = false) String comment,
                               @AuthenticationPrincipal CustomUserPrincipal principal) {
        claimService.approveClaim(id, comment != null ? comment : "Verified and approved.", principal.getUser());
        return "redirect:/admin/claims";
    }

    @PostMapping("/claims/{id}/reject")
    public String rejectClaim(@PathVariable Long id,
                              @RequestParam(required = false) String comment,
                              @AuthenticationPrincipal CustomUserPrincipal principal) {
        claimService.rejectClaim(id, comment != null ? comment : "Could not verify ownership.", principal.getUser());
        return "redirect:/admin/claims";
    }

    @GetMapping("/categories")
    public String categories(Model model) {
        model.addAttribute("categories", adminService.getAllCategories());
        return "admin/categories";
    }

    @PostMapping("/categories/add")
    public String addCategory(@RequestParam String name, RedirectAttributes redirectAttributes) {
        try {
            adminService.addCategory(name);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/categories";
    }

    @PostMapping("/categories/{id}/delete")
    public String deleteCategory(@PathVariable Long id) {
        adminService.deleteCategory(id);
        return "redirect:/admin/categories";
    }

    @GetMapping("/logs")
    public String logs(Model model) {
        model.addAttribute("logs", activityLogService.getRecentLogs());
        return "admin/logs";
    }
}