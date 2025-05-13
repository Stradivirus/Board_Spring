package com.example.board_sp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "board_archive")
@Getter
@Setter
@NoArgsConstructor
@IdClass(BoardArchiveId.class)
public class BoardArchive implements Serializable {

    @Id
    @Column(name = "id")
    private Long id;

    @Id
    @Column(name = "deleted_date", nullable = false)
    private LocalDate deletedDate;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    // writerId 컬럼(Long)으로 변경
    @Column(name = "writer_id", nullable = false)
    private Long writerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Member writer;

    @Column(name = "view_count", nullable = false)
    private Integer viewCount = 0;

    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDate createdDate;

    @Column(name = "created_time", nullable = false, updatable = false)
    private LocalTime createdTime;

    @Column(name = "deleted_time")
    private LocalTime deletedTime;
}