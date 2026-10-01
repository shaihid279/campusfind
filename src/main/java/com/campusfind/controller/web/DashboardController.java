package com.campusfind.controller.web;

import com.campusfind.dto.response.DashboardStatsDto;
import com.campusfind.entity.Claim;
import com.campusfind.entity.Item;
import com.campusfind.enums.ClaimStatus;
import com.campusfind.enums.ItemStatus;
import com.campusfind.enums.ItemType;
import com.campusfind.security.CustomUserPrincipal;
import com.campusfind.service.ClaimService;
import com.campusfind.service.ItemService;
import com.campusfind.service.NotificationService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DashboardController {

    private final ItemService itemService;
    private final ClaimService claimService;
    private final NotificationService notificationService;

    public DashboardController(ItemService itemService, ClaimService claimService,
                               NotificationService notificationService) {
        this.itemService = itemService;
        this.claimService = claimService;
        this.notificationService = notificationService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, @AuthenticationPrincipal CustomUserPrincipal principal) {
        List<Item> myItems = itemService.getMyItems(principal.getId());
        List<Claim> myClaims = claimService.getMyClaims(principal.getId());

        long lostCount = myItems.stream().filter(i -> i.getItemType() == ItemType.LOST).count();
        long foundCount = myItems.stream().filter(i -> i.getItemType() == ItemType.FOUND).count();
        long activeClaimsCount = myClaims.stream()
                .filter(c -> c.getStatus() == ClaimStatus.PENDING || c.getStatus() == ClaimStatus.UNDER_REVIEW)
                .count();
        long recoveredCount = myItems.stream().filter(i -> i.getStatus() == ItemStatus.RETURNED).count();

        DashboardStatsDto stats = new DashboardStatsDto(lostCount, foundCount, activeClaimsCount, recoveredCount);

        model.addAttribute("stats", stats);
        model.addAttribute("myItems", myItems);
        model.addAttribute("myClaims", myClaims);
        model.addAttribute("unreadNotifications", notificationService.getUnreadNotifications(principal.getId()));
        model.addAttribute("fullName", principal.getFullName());

        return "dashboard/student-dashboard";
    }
}