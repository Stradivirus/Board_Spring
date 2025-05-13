package com.example.board_sp.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ArchiveScheduler {

    private final JdbcTemplate jdbcTemplate;

    @Transactional
    @Scheduled(cron = "0 15 0 1 * *")
    public void archiveDeletedPosts() {
        try {
            List<Date> createdDates = jdbcTemplate.queryForList(
                    "SELECT DISTINCT created_date FROM board WHERE id IN (SELECT board_id FROM board_status WHERE deleted = true)",
                    Date.class
            );

            for (Date sqlDate : createdDates) {
                LocalDate date = sqlDate.toLocalDate();
                List<Long> ids = jdbcTemplate.queryForList(
                        "SELECT id FROM board WHERE created_date = ? AND id IN (SELECT board_id FROM board_status WHERE deleted = true AND created_date = ?)",
                        Long.class, date, date
                );
                for (Long id : ids) {
                    var deletedInfo = jdbcTemplate.queryForMap(
                            "SELECT deleted_date, deleted_time FROM board_status WHERE board_id = ? AND created_date = ? AND deleted = true ORDER BY revision DESC LIMIT 1",
                            id, date
                    );
                    LocalDate deletedDate = ((Date) deletedInfo.get("deleted_date")).toLocalDate();
                    LocalTime deletedTime = ((Time) deletedInfo.get("deleted_time")).toLocalTime();

                    migrateDeletedData(id, date, deletedDate, deletedTime);
                }
            }
        } catch (Exception e) {
            log.error("[ArchiveScheduler] 아카이브 실패: {}", e.getMessage(), e);
            throw new RuntimeException("삭제 게시글 이관 실패", e);
        }
    }

    private void migrateDeletedData(Long id, LocalDate createdDate, LocalDate deletedDate, LocalTime deletedTime) {
        String insertSql =
                "INSERT INTO board_archive " +
                        "(id, title, content, writer_id, view_count, created_date, created_time, deleted_date, deleted_time) " +
                        "SELECT id, title, content, writer_id, view_count, created_date, created_time, ?, ? " +
                        "FROM board WHERE id = ? AND created_date = ?";

        String deleteStatusSql = "DELETE FROM board_status WHERE board_id = ? AND created_date = ?";
        String deleteBoardSql = "DELETE FROM board WHERE id = ? AND created_date = ?";

        jdbcTemplate.update(insertSql, deletedDate, deletedTime, id, createdDate);
        jdbcTemplate.update(deleteStatusSql, id, createdDate);
        jdbcTemplate.update(deleteBoardSql, id, createdDate);

        log.info("[ArchiveScheduler] board → board_archive 이관 및 삭제 완료 (id: {}, created_date: {}, deleted_date: {}, deleted_time: {})",
                id, createdDate, deletedDate, deletedTime);
    }
}