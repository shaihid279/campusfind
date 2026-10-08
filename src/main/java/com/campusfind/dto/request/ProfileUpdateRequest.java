package com.campusfind.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class ProfileUpdateRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    private String department;
    private String year;
    private String phone;
    private MultipartFile photo;
}