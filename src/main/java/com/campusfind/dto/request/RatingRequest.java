package com.campusfind.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RatingRequest {

    @NotNull(message = "Please select a rating")
    @Min(1) @Max(5)
    private Integer rating;

    private String comment;
}