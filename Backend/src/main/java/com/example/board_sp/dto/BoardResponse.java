// board_sp/src/main/java/com/example/board_sp/dto/PostResponse.java
package com.example.board_sp.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class BoardResponse {
    private Long id;
    private String title;
    private String content;
    private Long writerId;
    private String writerNickname;
    private Integer viewCount;
    private LocalDate createdDate;
    private LocalTime createdTime;

    // board_status 테이블 (이력/상태)
    private Integer revision;
    private LocalDate updatedDate;
    private LocalTime updatedTime;
    private Boolean deleted;
    private LocalDate deletedDate;
    private LocalTime deletedTime;
}