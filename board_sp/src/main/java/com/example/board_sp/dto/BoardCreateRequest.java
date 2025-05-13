package com.example.board_sp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardCreateRequest {
    private String title;
    private String content;
    private String userId; // Long writerId → String userId
}