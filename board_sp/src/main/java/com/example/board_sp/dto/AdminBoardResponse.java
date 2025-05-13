package com.example.board_sp.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class AdminBoardResponse {
    private Long id;
    private String title;
    private String content;
    private Long writerId;
    private String writerNickname;
    private Integer viewCount;
    private LocalDate createdDate;
    private LocalTime createdTime;
    private LocalDate deletedDate;
    private LocalTime deletedTime;
}