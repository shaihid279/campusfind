package com.campusfind.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;

@Getter
@Setter
@AllArgsConstructor
public class AdminStatsDto {
    private long totalUsers;
    private long lostReports;
    private long foundReports;
    private long pendingClaims;
    private long returnedItems;
    private double recoveryRate;
}