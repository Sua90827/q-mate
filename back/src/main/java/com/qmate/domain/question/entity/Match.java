package com.qmate.domain.question.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

//TODO 임시 엔티티
@Entity
@Table(name = "match_temp")
public class Match {
  @Id
  private Long matchId;
}
