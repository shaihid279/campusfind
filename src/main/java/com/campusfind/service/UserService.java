package com.campusfind.service;

import com.campusfind.dto.request.ProfileUpdateRequest;
import com.campusfind.dto.request.RegisterRequest;
import com.campusfind.entity.User;
import com.campusfind.enums.Role;
import com.campusfind.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileStorageService fileStorageService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       FileStorageService fileStorageService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.fileStorageService = fileStorageService;
    }

    public void registerUser(RegisterRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        String collegeId = request.getCollegeId().trim();

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("An account with this email already exists.");
        }
        if (userRepository.existsByCollegeId(collegeId)) {
            throw new IllegalArgumentException("This Student/Staff ID is already registered.");
        }
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match.");
        }

        User user = new User();
        user.setFullName(request.getFullName().trim());
        user.setEmail(email);
        user.setCollegeId(collegeId);
        user.setDepartment(request.getDepartment());
        user.setYear(request.getYear());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.STUDENT);
        user.setEnabled(true);

        String photoUrl = fileStorageService.store(request.getPhoto(), "profiles");
        user.setProfilePhotoUrl(photoUrl);

        userRepository.save(user);
    }

    public void updateProfile(Long userId, ProfileUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        user.setFullName(request.getFullName().trim());
        user.setDepartment(request.getDepartment());
        user.setYear(request.getYear());
        user.setPhone(request.getPhone());

        if (request.getPhoto() != null && !request.getPhoto().isEmpty()) {
            String photoUrl = fileStorageService.store(request.getPhoto(), "profiles");
            user.setProfilePhotoUrl(photoUrl);
        }

        userRepository.save(user);
    }
}