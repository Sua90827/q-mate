package com.qmate.api;

import com.qmate.common.constants.user.UserProfileConstants;
import com.qmate.domain.user.ProfileService;
import com.qmate.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/me")
@Tag(name = "User Profile", description = "User 데이터 변경 API")
public class UserProfileController {
  private final ProfileService profileService;

  @PatchMapping("/profile")
  @Operation(
      summary = "프로필 데이터 수정(nickname, birthdate)",
      description = UserProfileConstants.PROFILE_MD
  )
  public UpdateProfileRes updateProfile(@AuthenticationPrincipal UserPrincipal me,
      @RequestBody UpdateProfileReq req) {
    boolean updated = profileService.updateProfile(me.userId(), normalizeNick(req.nickname()), parseDate(req.birthDate()));
    return new UpdateProfileRes(me.userId(), updated);
  }

  @PatchMapping("/nickname")
  @Operation(
      summary = "별명만 수정",
      description = UserProfileConstants.NICKNAME_MD
  )
  public UpdateNicknameRes updateNickname(@AuthenticationPrincipal UserPrincipal me, @RequestBody UpdateNicknameReq req) {
    boolean updated = profileService.updateProfile(me.userId(), normalizeNick(req.nickname()), null);
    String changedNick = profileService.findValue(me.userId());
    return new UpdateNicknameRes(me.userId(), updated, changedNick);
  }

  private String normalizeNick(String s) {
    if (s == null) return null;
    String t = s.trim();
    return t.isEmpty() ? null : t;
  }
  private LocalDate parseDate(String s) {
    if (s == null || s.isBlank()) return null;
    return LocalDate.parse(s);
  }

  public record UpdateProfileReq(String nickname, String birthDate) {}
  public record UpdateProfileRes(Long id, boolean updated) {}
  public record UpdateNicknameReq(String nickname) {}
  public record UpdateNicknameRes(Long id, boolean updated, String nickname) {}
}

