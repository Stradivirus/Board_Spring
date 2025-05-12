package com.example.board_sp.dto;

import lombok.Data;

@Data
public class MemberJoinRequest {
    private String userId;
    private String nickname;
    private String password;
    private String email;
}
