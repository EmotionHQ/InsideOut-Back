package com.example.Insideout.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ORSRequest {
    private Long sessionId;
    private Integer orsScore;
    private String userId;
}
