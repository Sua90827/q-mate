package com.qmate.domain.match;

import com.qmate.exception.custom.matchinstance.MatchRecoveryExpiredException;
import com.qmate.exception.custom.matchinstance.MatchStateConflictException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
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

  @OneToOne(mappedBy = "match", cascade = CascadeType.ALL, orphanRemoval = true)
  private MatchSetting matchSetting;

  //연관관계 평의 메서드
  public void addMember(MatchMember member) {
    this.members.add(member);
    member.setMatch(this);
  }

  public void setStatus(MatchStatus status) {
    this.status = status;
  }
  public void setMatchSetting(MatchSetting matchSetting){
    this.matchSetting = matchSetting;
  }

  public void updateStartDate(LocalDate startDate) {
    this.startDate = startDate.atStartOfDay();
  }

  public void disconnect(){
    //이미 끊어진 관계를 또 끊으려고 하는 것 방지
    if (this.status != MatchStatus.ACTIVE){
      throw new MatchStateConflictException();
    }
    this.status = MatchStatus.DETACHED_PENDING_DELETE;
    this.detachedAt = LocalDateTime.now();
    this.members.forEach(MatchMember::resetAgreement);
  }
  //끊어진 매칭 연결을 복구합니다.
  public boolean attemptToRestore() {
    //복구할 수 있는 상태인지 확인
    if (this.status != MatchStatus.DETACHED_PENDING_DELETE) {
      throw new MatchStateConflictException();
    }
    if (this.detachedAt != null && this.detachedAt.plusWeeks(2).isBefore(LocalDateTime.now())) {
      throw new MatchRecoveryExpiredException();
    }
    boolean allAgreed = this.members.stream().allMatch(MatchMember::isAgreed);

    if (allAgreed) {
      this.status = MatchStatus.ACTIVE;
      this.detachedAt = null;
      return true;
    }
//아직 상대방이 동의하지 않았다면, 상태를 변경하지 않고 '아직 대기 중'임을 알림
    return false;
  }

  // 상태 값 삭제로 변경 및 detachedAt 업데이트
  public void markAsDeleted(){
    this.status = MatchStatus.BROKEN;
    this.deletedAt = LocalDateTime.now();
  }


  //정적 팩토리 메서드 추가
  public static Match create(RelationType relationType, LocalDateTime startDate) {
    return Match.builder()
        .relationType(relationType)
        .startDate(startDate)
        .build();
  }

}
