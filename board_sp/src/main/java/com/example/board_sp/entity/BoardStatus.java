package com.example.board_sp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "board_status")
@Getter
@Setter
@NoArgsConstructor
@IdClass(BoardStatusId.class)
public class BoardStatus implements Serializable {

    @Id
    @Column(name = "board_id", nullable = false)
    private Long boardId;

    @Id
    @Column(name = "created_date", nullable = false)
    private LocalDate createdDate;

    @Id
    @Column(name = "revision", nullable = false)
    private Integer revision;

    @Column(name = "updated_date")
    private LocalDate updatedDate;

    @Column(name = "updated_time")
    private LocalTime updatedTime;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

    @Column(name = "deleted_date")
    private LocalDate deletedDate;

    @Column(name = "deleted_time")
    private LocalTime deletedTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "board_id", referencedColumnName = "id", insertable = false, updatable = false),
            @JoinColumn(name = "created_date", referencedColumnName = "created_date", insertable = false, updatable = false)
    })
    private Board board;
}