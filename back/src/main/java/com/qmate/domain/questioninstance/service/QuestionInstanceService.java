package com.qmate.domain.questioninstance.service;

import com.qmate.domain.match.Match;
import com.qmate.domain.questioninstance.entity.Answer;
import com.qmate.domain.questioninstance.entity.InstanceStatus;
import com.qmate.domain.questioninstance.entity.QuestionInstance;
import com.qmate.domain.questioninstance.mapper.QIDetailMapper;
import com.qmate.domain.questioninstance.model.response.QIDetailResponse;
import com.qmate.domain.questioninstance.model.response.QIListItem;
import com.qmate.domain.questioninstance.repository.AnswerRepository;
import com.qmate.domain.questioninstance.repository.QuestionInstanceRepository;
import com.qmate.domain.user.User;
import com.qmate.domain.user.UserRepository;
import com.qmate.exception.custom.UserNotFoundException;
import com.qmate.exception.custom.questioninstance.QuestionInstanceForbiddenException;
import com.qmate.exception.custom.questioninstance.QuestionInstanceNotFoundException;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QuestionInstanceService {

  private final QuestionInstanceRepository qiRepository;
  private final AnswerRepository answerRepository;
  private final UserRepository userRepository;

  @Transactional(readOnly = true)
  public QIDetailResponse getDetail(Long qiId, Long requesterId) {

    // 1) QI + (question/custom + category) + match 한 번에 로드
    QuestionInstance qi = qiRepository.findDetailWithQuestionAndMatch(qiId)
        .orElseThrow(QuestionInstanceNotFoundException::new);

    Match match = qi.getMatch();
    Long matchId = match.getMatchId();

    // 2) 요청자 조회 및 권한 확인: 현재 매치 == QI 매치
    User me = userRepository.findById(requesterId)
        .orElseThrow(UserNotFoundException::new);

    Long myCurrentMatchId = me.getCurrentMatchId();
    if (!matchId.equals(myCurrentMatchId)) {
      throw new QuestionInstanceForbiddenException();
    }

    // 3) 파트너 조회: 같은 매치의 '나 제외' 사용자
    User partner = userRepository.findByCurrentMatchIdAndIdNot(matchId, requesterId)
        .orElseThrow(UserNotFoundException::new);

    // 4) 답변 조회: (qiId, userId) 각각 단건
    Answer myAnswer = answerRepository
        .findByQuestionInstance_IdAndUser_Id(qiId, requesterId)
        .orElse(null);

    Answer partnerAnswer = answerRepository
        .findByQuestionInstance_IdAndUser_Id(qiId, partner.getId())
        .orElse(null);

    // 5) 가시성 결정 (정책)
    InstanceStatus status = qi.getStatus();
    boolean myVisible = status != InstanceStatus.EXPIRED;        // EXPIRED면 내 답변도 비공개/삭제
    boolean partnerVisible = status == InstanceStatus.COMPLETED; // 완료일 때만 상대 공개

    // 6) DTO 변환 (매퍼는 순수 변환)
    return QIDetailMapper.toResponse(
        qi, match, me, partner, myAnswer, partnerAnswer, myVisible, partnerVisible
    );
  }

  @Transactional(readOnly = true)
  public Page<QIListItem> list(Long userId, Long matchId, InstanceStatus status,
      LocalDateTime from, LocalDateTime to, Pageable pageable) {

    // 1) 요청자 조회 및 권한 확인: 현재 매치 == 조회 매치
    Long myCurrentMatchId = userRepository.findCurrentMatchIdById(userId).orElseThrow(UserNotFoundException::new);
    if (!Objects.equals(myCurrentMatchId, matchId)) {
      throw new QuestionInstanceForbiddenException();
    }

    return qiRepository.findList(matchId, status, from, to, pageable);
  }
}
