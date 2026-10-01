package com.campusfind.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;

@Getter
@Setter
@AllArgsConstructor
public class MatchResultDto {
    private Long matchedItemId;
    private String matchedItemCode;
    private String matchedItemTitle;
    private String matchedItemLocation;
    private int matchPercentage;
}