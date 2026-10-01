package com.campusfind.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;

@Getter
@Setter
@AllArgsConstructor
public class DashboardStatsDto {
    private long lostCount;
    private long foundCount;
    private long activeClaimsCount;
    private long recoveredCount;
}