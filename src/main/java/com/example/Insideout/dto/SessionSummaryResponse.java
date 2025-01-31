package com.example.Insideout.dto;

import com.example.Insideout.entity.Session.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SessionSummaryResponse {
    private Integer orsScore;
    private String summary;
    private Status status;
    private String feedback;
}