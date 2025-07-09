package com.example.board_sp.dto;

import lombok.Data;

@Data
public class MemberLoginRequest {
    private String userId;
    private String password;
}
