package com.qmate.schedule;

import com.qmate.domain.event.service.EventAlarmService;
import java.time.LocalDate;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventAlarmJob {
  private static final ZoneId KST = ZoneId.of("Asia/Seoul");

  private final EventAlarmService eventAlarmService;

  /**
   * 매일 00:10 KST 실행
   * cron: 초 분 시 일 월 요일
   * 일정 알림 생성 및 전송
   */
  @Scheduled(cron = "0 10 0 * * *", zone = "Asia/Seoul")
  public void processDailyEventAlarms() {
    LocalDate todayKst = LocalDate.now(KST);
    try {
      int created = eventAlarmService.registerAndSendAll(todayKst);
      log.info("[EventAlarmJob] {} notifications created ({}).", created, todayKst);
    } catch (Exception e) {
      // 서비스 내부에서 푸시는 afterCommit + try/catch 처리됨
      log.error("[EventAlarmJob] failed. date={}", todayKst, e);
    }
  }
}
