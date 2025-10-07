package com.qmate.domain.pet.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.qmate.domain.match.Match;
import com.qmate.domain.pet.entity.Pet;
import com.qmate.domain.pet.entity.PetExpLog;
import com.qmate.domain.pet.repository.PetExpLogRepository;
import com.qmate.domain.pet.repository.PetRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PetServiceTest {

  @InjectMocks
  private PetService sut;

  @Mock
  private PetRepository petRepository;
  @Mock
  private PetExpLogRepository petExpLogRepository;

  @Test
  @DisplayName("펫 생성 성공: Match 객체를 받아 새로운 Pet을 저장한다")
  void createPetForMatch_success() {
    // given
    Match match = Match.builder().id(1L).build();

    // when
    sut.createPetForMatch(match);

    // then: petRepository.save가 Pet 클래스 타입의 객체로 호출되었는지 검증
    verify(petRepository).save(any(Pet.class));
  }

  @Test
  @DisplayName("경험치 획득 성공: Pet의 경험치가 오르고 로그가 저장된다")
  void addExperienceForAnswerCompletion_success() {
    // given
    Match match = Match.builder().id(1L).build();
    Pet pet = Pet.builder()
        .match(match)
        .build(); // 테스트를 위해 실제 Pet 객체 생성

    given(petRepository.findByMatch(match)).willReturn(Optional.of(pet));

    // when
    sut.addExperienceForAnswerCompletion(match);

    // then
    assertThat(pet.getExp()).isEqualTo(10); // 경험치가 10 올랐는지 확인
    verify(petExpLogRepository).save(any(PetExpLog.class)); // 로그가 저장되었는지 확인
  }
}
