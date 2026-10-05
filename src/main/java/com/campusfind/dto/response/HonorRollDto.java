package com.campusfind.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;

@Getter
@Setter
@AllArgsConstructor
public class HonorRollDto {
    private Long userId;
    private String fullName;
    private String department;
    private String photoUrl;
    private double averageRating;
    private long totalReturns;
}