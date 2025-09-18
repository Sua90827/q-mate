package com.qmate.domain.question.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

//TODO 임시 엔티티
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "match_temp")
public class Match {
  @Id
  private Long id;

  private RelationType relationType;
}
