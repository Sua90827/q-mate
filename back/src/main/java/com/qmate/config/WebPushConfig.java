package com.qmate.config;

import com.qmate.common.push.NoopPushSender;
import com.qmate.common.push.PushSender;
import com.qmate.common.push.WebPushSender;
import com.qmate.domain.notification.repository.PushSubscriptionRepository;
import java.security.KeyPair;
import java.security.Security;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import lombok.extern.slf4j.Slf4j;
import nl.martijndwars.webpush.PushService;
import nl.martijndwars.webpush.Utils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class WebPushConfig {

  @Value("${webpush.vapid.subject}")
  private String subject;

  @Value("${webpush.vapid.public-key}")
  private String publicKey;

  @Value("${webpush.vapid.private-key}")
  private String privateKey;

  @Bean
  public PushSender pushSender(PushSubscriptionRepository pushSubscriptionRepository) {
    KeyPair keyPair = null;
    if (privateKey.isEmpty()) {
      log.warn("VAPID keys are not configured, WebPushSender will not be created.");
      return new NoopPushSender();
    }
    if (Security.getProvider("BC") == null) {
      Security.addProvider(new BouncyCastleProvider());
    }
    try {
      keyPair = new KeyPair(
          Utils.loadPublicKey(publicKey),
          Utils.loadPrivateKey(privateKey)
      );
    } catch (Exception e) {
      log.error("Failed to load VAPID keys", e);
      throw new RuntimeException("Failed to load VAPID keys", e);
    }
    PushService service = new PushService();
    service.setSubject(subject);
    service.setPublicKey(keyPair.getPublic());
    service.setPrivateKey(keyPair.getPrivate());
    return new WebPushSender(service, pushSubscriptionRepository);
  }
}
