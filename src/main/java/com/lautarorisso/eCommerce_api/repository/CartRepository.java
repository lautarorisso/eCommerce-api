package com.lautarorisso.eCommerce_api.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lautarorisso.eCommerce_api.enums.CartStatus;
import com.lautarorisso.eCommerce_api.model.CartEntity;

public interface CartRepository extends JpaRepository<CartEntity, Long> {
  Optional<CartEntity> findByUserId(Long userId);

  boolean existsByUserId(Long userId);

  List<CartEntity> findByStatusAndLastActivityBefore(CartStatus status, LocalDateTime dateTime);
}
