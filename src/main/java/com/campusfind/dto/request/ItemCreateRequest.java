package com.campusfind.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class ItemCreateRequest {

    @NotBlank(message = "Item name is required")
    private String title;

    @NotNull(message = "Category is required")
    private Long categoryId;

    private String description;

    @NotNull(message = "Date is required")
    private LocalDate itemDate;

    private LocalTime itemTime;

    @NotBlank(message = "Location is required")
    private String location;

    private String color;
    private String brand;
    private String identifyingFeatures;
    private String storageLocation;
    private String additionalInformation;
    private MultipartFile image;
}