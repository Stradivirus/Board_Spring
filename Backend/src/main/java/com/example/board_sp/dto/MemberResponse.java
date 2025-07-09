package com.example.board_sp.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class MemberResponse {
    private Long id;
    private String userId;
    private String nickname;
    private String email;
    private LocalDate joinedAt;

}