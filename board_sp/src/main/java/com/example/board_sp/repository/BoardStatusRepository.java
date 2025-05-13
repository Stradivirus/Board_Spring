package com.example.board_sp.repository;

import com.example.board_sp.entity.BoardStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BoardStatusRepository extends JpaRepository<BoardStatus, Long> {
    @Query("""
        SELECT bs FROM BoardStatus bs
        WHERE bs.deleted = true
          AND bs.revision = (
            SELECT MAX(bs2.revision)
            FROM BoardStatus bs2
            WHERE bs2.boardId = bs.boardId AND bs2.createdDate = bs.createdDate
          )
        """)
    Page<BoardStatus> findSoftDeletedLatest(Pageable pageable);
}