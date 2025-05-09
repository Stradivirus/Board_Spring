package com.example.board_sp.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class BoardStatusId implements Serializable {
    private Long boardId;
    private LocalDate createdDate;
    private Integer revision;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BoardStatusId)) return false;
        BoardStatusId that = (BoardStatusId) o;
        return Objects.equals(boardId, that.boardId) &&
                Objects.equals(createdDate, that.createdDate) &&
                Objects.equals(revision, that.revision);
    }

    @Override
    public int hashCode() {
        return Objects.hash(boardId, createdDate, revision);
    }
}