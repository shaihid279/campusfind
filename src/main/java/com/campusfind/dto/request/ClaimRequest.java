package com.campusfind.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClaimRequest {

    @NotBlank(message = "Please describe a unique feature of the item")
    private String uniqueFeature;

    private String approximatePurchaseDate;

    private String previousLocation;

    private String serialNumber;
}