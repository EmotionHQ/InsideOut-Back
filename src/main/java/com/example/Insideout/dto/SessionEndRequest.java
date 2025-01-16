package com.example.Insideout.dto;

import com.example.Insideout.entity.Session.AgreementType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SessionEndRequest {
    private Long sessionId;
    private Integer srsScore;
    private AgreementType agreement;
}