package com.qmate.api.pet;

import com.qmate.common.constants.HttpStatusCode;
import com.qmate.domain.pet.model.response.PetExpResponse;
import com.qmate.domain.pet.service.PetService;
import com.qmate.exception.MatchErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/matches")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Pet", description = "펫 API")
public class PetController {
  private final PetService petService;

  @Operation(
      summary = "매치 ID로 펫 경험치 조회",
      description = "매치 ID로 펫 경험치를 조회합니다.\n\n"
          + "### 에러 응답\n\n"
          + "| HTTP | errorCode | message |\n"
          + "|-----:|-----------|---------|\n"
          + "| " + HttpStatusCode.NOT_FOUND + " | " + MatchErrorCode.MATCH_NOT_FOUND_ERROR_CODE + " | "
          + MatchErrorCode.MATCH_NOT_FOUND_MESSAGE + " |\n"
  )
  @GetMapping("/{matchId}/pet")
  public ResponseEntity<PetExpResponse> getExp(@PathVariable Long matchId) {
    return ResponseEntity.ok(petService.getExpByMatchId(matchId));
  }
}
