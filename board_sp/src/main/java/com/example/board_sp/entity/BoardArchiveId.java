package com.example.board_sp.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class BoardArchiveId implements Serializable {
    private Long id;
    private LocalDate deletedDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BoardArchiveId)) return false;
        BoardArchiveId that = (BoardArchiveId) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(deletedDate, that.deletedDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, deletedDate);
    }
}