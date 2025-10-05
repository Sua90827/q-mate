package com.qmate.domain.pet.service;

import com.qmate.domain.match.Match;
import com.qmate.domain.pet.entity.Pet;
import com.qmate.domain.pet.model.response.PetExpResponse;
import com.qmate.domain.pet.repository.PetRepository;
import com.qmate.exception.custom.matchinstance.MatchNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PetService {

  private final PetRepository petRepository;

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
}
