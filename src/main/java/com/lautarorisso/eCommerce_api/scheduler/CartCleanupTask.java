package com.lautarorisso.eCommerce_api.scheduler;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.lautarorisso.eCommerce_api.enums.CartStatus;
import com.lautarorisso.eCommerce_api.repository.CartRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class CartCleanupTask {

  private final CartRepository cartRepository;

  @Scheduled(cron = "0 0 */6 * * *")
  @Transactional
  public void abandonStaleCarts() {
    var threshold = LocalDateTime.now().minusHours(24);
    var staleCarts = cartRepository.findByStatusAndLastActivityBefore(CartStatus.ACTIVE, threshold);
    staleCarts.forEach(cart -> cart.abandon());
    cartRepository.saveAll(staleCarts);
    if (!staleCarts.isEmpty()) {
      log.info("Abandoned {} stale cart(s)", staleCarts.size());
    }
  }
}
