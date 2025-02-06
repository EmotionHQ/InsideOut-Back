package com.example.Insideout.dto;

import com.example.Insideout.entity.Session.Status;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SessionSummaryResponse {
    private Integer orsScore;
    private String summary;
    private Status status;
    private String feedback;
    private LocalDateTime createdAt;
}