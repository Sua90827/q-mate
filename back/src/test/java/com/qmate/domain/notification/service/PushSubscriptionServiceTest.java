package com.qmate.domain.notification.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

import com.qmate.domain.notification.entity.PushSubscription;
import com.qmate.domain.notification.model.request.PushSubscriptionRegisterRequest;
import com.qmate.domain.notification.model.request.PushSubscriptionRegisterRequest.Keys;
import com.qmate.domain.notification.model.response.PushSubscriptionRegisterResponse;
import com.qmate.domain.notification.repository.PushSubscriptionRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PushSubscriptionServiceTest {
  @Mock
  PushSubscriptionRepository repository;

  @InjectMocks
  PushSubscriptionService service;

  private static PushSubscriptionRegisterRequest request(String endpoint) {
    return PushSubscriptionRegisterRequest.builder()
        .endpoint(endpoint)
        .keys(Keys.builder()
            .p256dh("B" + "x".repeat(87)) // 더미 88자
            .auth("A" + "y".repeat(23))   // 더미 24자
            .build())
        .build();
  }

  @Test
  @DisplayName("새 엔드포인트면 새 구독을 생성한다")
  void upsert_creates_new_subscription_when_not_exists() {
    // given
    long userId = 1L;
    String endpoint = "https://fcm.googleapis.com/fcm/send/NEW";
    var req = request(endpoint);
    byte[] hash = PushSubscription.sha256(endpoint);

    given(repository.findByEndpointHash(hash)).willReturn(Optional.empty());

    var saved = PushSubscription.builder()
        .id(10L).userId(userId).endpoint(endpoint).endpointHash(hash)
        .keyP256dh(req.getKeyP256dh()).keyAuth(req.getKeyAuth())
        .build();
    var now = LocalDateTime.now();
    saved.setCreatedAt(now);
    saved.setUpdatedAt(now);

    given(repository.save(Mockito.argThat(ps ->
        ps.getUserId().equals(userId) && ps.getEndpoint().equals(endpoint)))).willReturn(saved);

    // when
    PushSubscriptionRegisterResponse resp = service.upsert(userId, req);

    // then
    assertThat(resp.getSubscriptionId()).isEqualTo(10L);
    assertThat(resp.getCreatedAt()).isNotNull();
    assertThat(resp.getUpdatedAt()).isNotNull();
    then(repository).should().findByEndpointHash(hash);
    then(repository).should().save(Mockito.any(PushSubscription.class));
  }

  @Test
  @DisplayName("기존 엔드포인트면 소유권을 새 사용자로 이전하고 키를 최신화한다")
  void upsert_transfers_ownership_and_refreshes_keys_when_exists() {
    // given
    long oldUser = 111L;
    long newUser = 222L;
    String endpoint = "https://fcm.googleapis.com/fcm/send/SAME";
    var req = request(endpoint);
    byte[] hash = PushSubscription.sha256(endpoint);

    var exist = PushSubscription.builder()
        .id(55L).userId(oldUser).endpoint(endpoint).endpointHash(hash)
        .keyP256dh("OLD_P").keyAuth("OLD_A")
        .build();

    given(repository.findByEndpointHash(hash)).willReturn(Optional.of(exist));

    var saved = PushSubscription.builder()
        .id(55L).userId(newUser).endpoint(endpoint).endpointHash(hash)
        .keyP256dh(req.getKeyP256dh()).keyAuth(req.getKeyAuth())
        .build();
    saved.setCreatedAt(LocalDateTime.now().minusDays(1));
    saved.setUpdatedAt(LocalDateTime.now());
    given(repository.save(exist)).willReturn(saved);

    // when
    PushSubscriptionRegisterResponse resp = service.upsert(newUser, req);

    // then
    assertThat(resp.getSubscriptionId()).isEqualTo(55L);
    assertThat(exist.getUserId()).isEqualTo(newUser);
    assertThat(exist.getKeyP256dh()).isEqualTo(req.getKeyP256dh());
    assertThat(exist.getKeyAuth()).isEqualTo(req.getKeyAuth());
    then(repository).should().save(exist);
  }

  @Test
  @DisplayName("엔드포인트 기반 해지: 현재 사용자 소유일 때만 삭제된다")
  void unsubscribe_deletes_only_when_owned_by_request_user() {
    // given
    long userId = 1L;
    String endpoint = "https://fcm.googleapis.com/fcm/send/DEL";
    byte[] hash = PushSubscription.sha256(endpoint);

    var owned = PushSubscription.builder()
        .id(9L).userId(userId).endpoint(endpoint).endpointHash(hash)
        .keyP256dh("P").keyAuth("A").build();

    given(repository.findByEndpointHash(hash)).willReturn(Optional.of(owned));

    // when
    service.unsubscribe(userId, endpoint);

    // then
    then(repository).should().delete(owned);
  }

  @Test
  @DisplayName("엔드포인트 기반 해지: 다른 사용자 소유면 삭제하지 않는다")
  void unsubscribe_does_nothing_when_owned_by_other_user() {
    // given
    long me = 1L;
    long other = 2L;
    String endpoint = "https://fcm.googleapis.com/fcm/send/KEEP";
    byte[] hash = PushSubscription.sha256(endpoint);

    var notOwned = PushSubscription.builder()
        .id(10L).userId(other).endpoint(endpoint).endpointHash(hash)
        .keyP256dh("P").keyAuth("A").build();

    given(repository.findByEndpointHash(hash)).willReturn(Optional.of(notOwned));

    // when
    service.unsubscribe(me, endpoint);

    // then
    then(repository).should(never()).delete(any());
  }
}
