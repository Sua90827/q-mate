package com.qmate.domain.user;

import com.qmate.domain.match.Match;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "`user`") // user가 예약어라 백틱 처리
public class User {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_id", nullable = false)
  private Long id;

  @Email
  @NotBlank
  @Column(nullable = false, unique = true)
  private String email;

  /*
   * local login - bcrypt 해시 저장
   * social login - null 허용
   */
  private String passwordHash;

  @NotBlank
  @Size(max = 50)
  @Column(nullable = false, length = 50)
  private String nickname;

  private LocalDate birthDate;

  @Builder.Default
  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 10)
  private Role role = Role.USER;

  private Long currentMatchId;

  @Builder.Default
  @Column(name = "push_enabled", nullable = false)
  private boolean pushEnabled = false;

  @CreationTimestamp
  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(nullable = false)
  private LocalDateTime updatedAt;

  public enum Role {
    USER, ADMIN
  }
  //특정 매칭에 참여할 때 currentMatchId를 업데이트.
  public void joinMatch(Match match){
    this.currentMatchId = match.getId();
  }
  //매칭에서 연결이 끊어지거나 탈퇴할 때 currentMatchId를 null로 초기화 합니다.
  public void leaveMatch(){
    this.currentMatchId = null;
  }
}