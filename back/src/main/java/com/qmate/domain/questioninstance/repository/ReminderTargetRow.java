package com.qmate.domain.questioninstance.repository;

public interface ReminderTargetRow {

  Long getQiId();

  Long getMatchId();

  Long getUserId();

  Boolean getPushEnabled();
}