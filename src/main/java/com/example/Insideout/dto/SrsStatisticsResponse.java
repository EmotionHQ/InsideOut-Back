package com.example.Insideout.dto;

import java.time.LocalDate;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SrsStatisticsResponse {
    private Map<LocalDate, SrsStats> weeklyStatistics;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class SrsStats {
        private double average;
        private double variance;
    }
}