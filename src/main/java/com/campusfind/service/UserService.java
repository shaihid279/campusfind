package com.campusfind.service;

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

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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

        userRepository.save(user);
    }
}