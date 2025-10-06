package com.qmate.domain.pet.service;

import com.qmate.domain.match.Match;
import com.qmate.domain.pet.entity.Pet;
import com.qmate.domain.pet.entity.PetExpLog;
import com.qmate.domain.pet.entity.PetExpReason;
import com.qmate.domain.pet.model.response.PetExpResponse;
import com.qmate.domain.pet.repository.PetExpLogRepository;
import com.qmate.domain.pet.repository.PetRepository;
import com.qmate.exception.custom.matchinstance.MatchNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PetService {

  private final PetRepository petRepository;
  private final PetExpLogRepository petExpLogRepository;
  /**
   * matchId로 펫 경험치 조회
   */
  @Transactional(readOnly = true)
  public PetExpResponse getExpByMatchId(Long matchId) {
    Pet pet = petRepository.findByMatch_Id(matchId)
        .orElseThrow(MatchNotFoundException::new);
    return PetExpResponse.builder()
        .exp(pet.getExp())
        .build();
  }
  //특정 매칭에 대한 새로운 펫을 생성합니다.
  @Transactional
  public void createPetForMatch(Match match){
    Pet newPet = Pet.builder()
            .match(match)
                .build();
    petRepository.save(newPet);
  }

  /**
   * 답변 완료 시 펫 경험치를 추가하고 로그를 남깁니다.
   * @param match 경험치를 받을 펫이 속한 Match
   */
  @Transactional
  public void addExperienceForAnswerCompletion(Match match) {
    // 해당 매칭의 펫을 찾습니다.
    petRepository.findByMatch(match).ifPresent(pet -> {
      int experienceAmount = 10; // 획득 경험치는 상수로 관리하는 것이 좋습니다.

      // 펫의 경험치를 올립니다.
      pet.addExp(experienceAmount);

      // 경험치 획득 로그를 생성합니다.
      PetExpLog log = PetExpLog.builder()
          .match(match)
          .delta(experienceAmount)
          .reason(PetExpReason.ANSWER_COMPLETED)
          .build();

      petExpLogRepository.save(log);
    });
  }
}
