package com.example.board_sp.repository;

import com.example.board_sp.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;

public interface BoardRepository extends JpaRepository<Board, Long> {

    // 삭제되지 않은 최신 revision 게시글 조회 (board + board_status 조인)
    @Query("""
        SELECT b FROM Board b
        JOIN BoardStatus s ON b.id = s.boardId AND b.createdDate = s.createdDate
        WHERE s.revision = (
            SELECT MAX(s2.revision) FROM BoardStatus s2
            WHERE s2.boardId = b.id AND s2.createdDate = b.createdDate
        )
        AND s.deleted = false
        ORDER BY b.createdDate DESC, b.createdTime DESC
    """)
    Page<Board> findAllActive(Pageable pageable);

    // 소프트 삭제(아직 아카이브 안 된 글)만 조회
    @Query("""
        SELECT b FROM Board b
        JOIN BoardStatus s ON b.id = s.boardId AND b.createdDate = s.createdDate
        WHERE s.revision = (
            SELECT MAX(s2.revision) FROM BoardStatus s2
            WHERE s2.boardId = b.id AND s2.createdDate = b.createdDate
        )
        AND s.deleted = true
        ORDER BY b.createdDate DESC, b.createdTime DESC
    """)
    Page<Board> findSoftDeleted(Pageable pageable);

    boolean existsByTitleAndWriterIdAndContentAndCreatedDate(String title, Long writerId, String content, LocalDate createdDate);
}