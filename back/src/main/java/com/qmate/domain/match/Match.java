package com.qmate.domain.match;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "`match`")
public class Match {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "match_id")
  private Long id;

  @Enumerated(EnumType.STRING)
  private RelationType relationType;

  @Enumerated(EnumType.STRING)
  @ColumnDefault("'WAITING'")
  @Builder.Default
  private MatchStatus status = MatchStatus.WAITING;

  private LocalDateTime startDate;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  private LocalDateTime detachedAt;
  private LocalDateTime deletedAt;

  //다른 테이블과의 관계
  @OneToMany(mappedBy = "match", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<MatchMember> members = new ArrayList<>();

  //연관관계 평의 메서드
  public void addMember(MatchMember member) {
    this.members.add(member);
    member.setMatch(this);
  }

  public void setStatus(MatchStatus status) {
    this.status = status;
  }

  //정적 팩토리 메서드 추가
  public static Match create(RelationType relationType, LocalDateTime startDate) {
    return Match.builder()
        .relationType(relationType)
        .startDate(startDate)
        .build();
  }

}
