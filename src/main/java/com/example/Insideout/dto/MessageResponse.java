package com.example.Insideout.dto;

import com.example.Insideout.entity.Message.AuthorType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MessageResponse {
    private String content;
    private AuthorType authorType;
    private LocalDateTime createdAt;
}
