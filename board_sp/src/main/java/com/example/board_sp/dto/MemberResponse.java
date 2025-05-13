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
    private LocalDate joinedAt; // 필드 추가

    // 이미 @Setter가 있으므로 setJoinedAt(LocalDate joinedAt) 자동 생성됨
}