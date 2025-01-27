package com.example.Insideout.dto;

import java.time.LocalDate;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class OrsStatisticsResponse {
    private Map<LocalDate, OrsStats> weeklyStatistics;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class OrsStats {
        private double average;
        private double variance;
    }
}