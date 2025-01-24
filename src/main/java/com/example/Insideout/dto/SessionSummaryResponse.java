package com.example.Insideout.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SessionSummaryResponse {
    private String summary;
    private String status;
    private String feedback;
}