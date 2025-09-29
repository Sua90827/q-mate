package com.qmate.domain.questioninstance.service;

import com.qmate.domain.match.Match;
import com.qmate.domain.match.MatchMember;
import com.qmate.domain.questioninstance.entity.Answer;
import com.qmate.domain.questioninstance.entity.QuestionInstance;
import com.qmate.domain.questioninstance.entity.QuestionInstanceStatus;
import com.qmate.domain.questioninstance.mapper.QIDetailMapper;
import com.qmate.domain.questioninstance.model.response.QIDetailResponse;
import com.qmate.domain.questioninstance.model.response.QIListItem;
import com.qmate.domain.questioninstance.repository.AnswerRepository;
import com.qmate.domain.questioninstance.repository.QuestionInstanceRepository;
import com.qmate.domain.user.User;
import com.qmate.exception.custom.questioninstance.QuestionInstanceNotFoundException;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuestionInstanceService {

  private final QuestionInstanceRepository qiRepository;
  private final AnswerRepository answerRepository;

  /**
   * QI 상세 조회
   *
   * @param qiId        질문 인스턴스 ID
   * @param requesterId 요청자(나) ID
   * @return QIDetailResponse
   * @throws QuestionInstanceNotFoundException QI 없음
   */
  public QIDetailResponse getDetail(Long qiId, Long requesterId) {

    // QI + (question/custom + category) + match + user 한 번에 로드
    // 요청자의 qi 권한 여부도 동시 체크
    QuestionInstance qi = qiRepository.findDetailWithMatchMembersAndQuestionByIdIfRequesterInMatch(qiId, requesterId)
        .orElseThrow(QuestionInstanceNotFoundException::new);

    Match match = qi.getMatch();

    // match에서 나와 파트너를 추출
    Iterator<MatchMember> it = match.getMembers().iterator();
    MatchMember a = it.next();
    MatchMember b = it.next();

    User me = Objects.equals(a.getUser().getId(), requesterId) ? a.getUser() : b.getUser();
    User partner = Objects.equals(a.getUser().getId(), requesterId) ? b.getUser() : a.getUser();

    // 4) 답변 조회: (qiId, userId) 각각 단건
    Answer myAnswer = answerRepository
        .findByQuestionInstance_IdAndUserId(qiId, requesterId)
        .orElse(null);

    Answer partnerAnswer = answerRepository
        .findByQuestionInstance_IdAndUserId(qiId, partner.getId())
        .orElse(null);

    // 5) 가시성 결정 (정책)
    QuestionInstanceStatus status = qi.getStatus();
    boolean myVisible = status != QuestionInstanceStatus.EXPIRED;        // EXPIRED면 내 답변도 비공개/삭제
    boolean partnerVisible = status == QuestionInstanceStatus.COMPLETED; // 완료일 때만 상대 공개

    // 6) DTO 변환 (매퍼는 순수 변환)
    return QIDetailMapper.toResponse(
        qi, match, me, partner, myAnswer, partnerAnswer, myVisible, partnerVisible
    );
  }

  /**
   * 매치의 최신 알림된 QI 상세 조회
   *
   * @param matchId     매치 ID
   * @param requesterId 요청자(나) ID
   * @return QIDetailResponse
   * @throws QuestionInstanceNotFoundException QI 없음
   */
  public QIDetailResponse getLatestDelivered(Long matchId, Long requesterId) {
    Long qiId = qiRepository.findLatestDeliveredIdByMatch(matchId)
        .orElseThrow(QuestionInstanceNotFoundException::new);
    return getDetail(qiId, requesterId);
  }

  /**
   * QI 목록 조회
   *
   * @param userId   요청자(나) ID
   * @param matchId  매치 ID (필수)
   * @param status   질문 인스턴스 상태 (optional)
   * @param from     deliveredAt 시작 범위 (inclusive, optional)
   * @param to       deliveredAt 종료 범위 (exclusive, optional)
   * @param pageable 페이지 정보
   * @return Page&lt;QIListItem&gt;
   */
  @Transactional(readOnly = true)
  public Page<QIListItem> list(Long userId, Long matchId, QuestionInstanceStatus status,
      LocalDateTime from, LocalDateTime to, Pageable pageable) {

    return qiRepository.findPageByMatchIdForRequesterWithQuestion(matchId, userId, status, from, to, pageable);
  }
}
