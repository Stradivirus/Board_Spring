package com.example.board_sp.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
@RequiredArgsConstructor
public class PartitionScheduler {

    private final JdbcTemplate jdbcTemplate;

    // board 테이블: 월별 파티션 (created_date 기준)
    @Scheduled(cron = "0 15 0 1 * *")
    public void createMonthlyBoardPartition() {
        LocalDate nextMonth = LocalDate.now().plusMonths(1);
        String suffix = nextMonth.format(DateTimeFormatter.ofPattern("yyyyMM"));
        LocalDate start = nextMonth.withDayOfMonth(1);
        LocalDate end = start.plusMonths(1);

        String sql = String.format(
                "CREATE TABLE IF NOT EXISTS board_%s PARTITION OF board " +
                        "FOR VALUES FROM ('%s') TO ('%s')",
                suffix, start, end
        );
        try {
            jdbcTemplate.execute(sql);
            log.info("[PartitionScheduler] board_{} 파티션 생성 성공", suffix);
        } catch (Exception e) {
            log.error("[PartitionScheduler] board_{} 파티션 생성 실패: {}", suffix, e.getMessage());
        }
    }

    // board_archive 테이블: 분기별 파티션 (deleted_date 기준)
    @Scheduled(cron = "0 0 0 1 1,4,7,10 *")
//    @Scheduled(cron = "0 * * * * *")
    public void createQuarterlyArchivePartition() {
        LocalDate nextQuarter = LocalDate.now().plusMonths(3);
        int year = nextQuarter.getYear();
        int quarter = (nextQuarter.getMonthValue() - 1) / 3 + 1;
        String suffix = String.format("%dq%d", year, quarter);

        LocalDate start = LocalDate.of(year, (quarter - 1) * 3 + 1, 1);
        LocalDate end = start.plusMonths(3);

        String sql = String.format(
                "CREATE TABLE IF NOT EXISTS board_archive_%s PARTITION OF board_archive " +
                        "FOR VALUES FROM ('%s') TO ('%s')",
                suffix, start, end
        );
        try {
            jdbcTemplate.execute(sql);
            log.info("[PartitionScheduler] board_archive_{} 파티션 생성 성공", suffix);
        } catch (Exception e) {
            log.error("[PartitionScheduler] board_archive_{} 파티션 생성 실패: {}", suffix, e.getMessage());
        }
    }
}