package com.example.board_sp.repository;

import com.example.board_sp.entity.BoardArchive;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface PostArchiveRepository extends JpaRepository<BoardArchive, Long> {

    // 삭제된 글 전체 조회 (최신순)
    @Query("""
                SELECT a FROM BoardArchive a
                ORDER BY a.deletedDate DESC, a.createdTime DESC
            """)
    Page<BoardArchive> findAllDeleted(Pageable pageable);

    // 삭제된 글 중 특정 기간 조회
    @Query("""
                SELECT a FROM BoardArchive a
                WHERE a.deletedDate >= :start AND a.deletedDate < :end
                ORDER BY a.deletedDate DESC, a.createdTime DESC
            """)
    Page<BoardArchive> findByDeletedDatePeriod(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end,
            Pageable pageable
    );
}