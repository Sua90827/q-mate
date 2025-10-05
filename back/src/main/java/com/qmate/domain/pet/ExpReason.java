package com.qmate.domain.pet;

public enum ExpReason {
  ANSWER_COMPLETED, // 두 멤버가 답변을 모두 완료했을 때
  ADMIN_ADJUST,     // 관리자가 수동으로 조정한 경우
  OTHER             // 기타
}
