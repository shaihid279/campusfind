package com.campusfind.controller.web;

import com.campusfind.dto.request.ProfileUpdateRequest;
import com.campusfind.repository.UserRepository;
import com.campusfind.security.CustomUserPrincipal;
import com.campusfind.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProfileController {

    private final UserService userService;
    private final UserRepository userRepository;

    public ProfileController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @GetMapping("/profile")
    public String showProfile(Model model, @AuthenticationPrincipal CustomUserPrincipal principal) {
        var user = userRepository.findById(principal.getId()).orElseThrow();
        ProfileUpdateRequest form = new ProfileUpdateRequest();
        form.setFullName(user.getFullName());
        form.setDepartment(user.getDepartment());
        form.setYear(user.getYear());
        form.setPhone(user.getPhone());

        model.addAttribute("user", user);
        model.addAttribute("profileRequest", form);
        return "profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@Valid @ModelAttribute("profileRequest") ProfileUpdateRequest request,
                                BindingResult result, Model model,
                                @AuthenticationPrincipal CustomUserPrincipal principal,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("user", userRepository.findById(principal.getId()).orElseThrow());
            return "profile";
        }
        userService.updateProfile(principal.getId(), request);
        redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
        return "redirect:/profile";
    }
}