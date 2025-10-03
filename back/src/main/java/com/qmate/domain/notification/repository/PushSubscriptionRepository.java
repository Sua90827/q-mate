package com.qmate.domain.notification.repository;

import com.qmate.domain.notification.entity.PushSubscription;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PushSubscriptionRepository extends JpaRepository<PushSubscription, Long> {
  Optional<PushSubscription> findByEndpointHash(byte[] endpointHash);
}