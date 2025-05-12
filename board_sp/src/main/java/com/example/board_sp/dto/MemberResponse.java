package com.example.board_sp.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class MemberResponse {
    private Long id;
    private String userId;
    private String nickname;
    private String email;
    private LocalDate joinedAt;
}
