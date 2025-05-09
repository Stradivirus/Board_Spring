package com.example.board_sp.repository;

import com.example.board_sp.entity.BoardStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface BoardStatusRepository extends JpaRepository<BoardStatus, Long> {

    // 해당 게시글의 최신 revision 번호 조회
    @Query("""
        SELECT COALESCE(MAX(s.revision), 0) FROM BoardStatus s
        WHERE s.boardId = :boardId AND s.createdDate = :createdDate
    """)
    int findMaxRevision(@Param("boardId") Long boardId, @Param("createdDate") LocalDate createdDate);
}