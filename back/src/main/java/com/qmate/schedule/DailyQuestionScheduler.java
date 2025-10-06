package com.qmate.schedule;

import com.qmate.domain.questioninstance.service.DailyQuestionGenerationService;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DailyQuestionScheduler {

  private static final ZoneId ZONE_KST = ZoneId.of("Asia/Seoul");
  private final DailyQuestionGenerationService generationService;

  /**
   * 매시 50분마다 실행, KST 기준. 예) 11:50에 실행되면 targetDelivery = 12:00 로 계산.
   */
  @Scheduled(cron = "0 50 * * * *", zone = "Asia/Seoul")
  public void runHourly50() {
    ZonedDateTime now = ZonedDateTime.now(ZONE_KST);
    generationService.generateForNextSlot(now);
  }
}
