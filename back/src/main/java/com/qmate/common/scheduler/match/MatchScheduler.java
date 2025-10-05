package com.qmate.common.scheduler.match;

import com.qmate.domain.match.service.MatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MatchScheduler {

  private final MatchService matchService;

  //매일 자정 0시 0분 0초에 이 메서드를 실행
  @Scheduled(cron = "0 0 0 * * *")
  public void runDisconnectInactiveMatches(){
    log.info("비활성 매칭 자동 연결 끊기 작업을 시작합니다...");
    try {
      matchService.disconnectInactiveMatches();
      log.info("비할성 매칭 자동 연결 끊기 작업을 성공적으로 완료했습니다.");
    } catch (Exception e){
      log.error("비활성 매칭 자동 연결 끊기 작업 중 오류가 발생했습니다.", e);
    }
  }
  //매일 자정 0시 0분 0초에 복구 기간 만료된 매칭을 확인
  @Scheduled(cron = "0 0 0 * * *")
  public void runFinalizeExpiredMatches(){
    log.info("복구 기간 만료된 매칭 데이터 정리 작업을 시작합니다...");
    try {
      matchService.finalizeExpiredMatches();
      log.info("복구 기간 만료된 매칭 데이터 정리 작업을 성공적으로 완료했습니다.");
    } catch (Exception e) {
      log.error("복구 기간 만료된 매칭 데이터 정리 작업 중 오류가 발생했습니다.", e);
    }

  }

}
