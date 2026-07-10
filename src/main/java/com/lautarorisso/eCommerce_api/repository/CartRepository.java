package com.lautarorisso.eCommerce_api.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lautarorisso.eCommerce_api.enums.CartStatus;
import com.lautarorisso.eCommerce_api.model.CartEntity;

public interface CartRepository extends JpaRepository<CartEntity, Long> {
  @Query("SELECT c FROM CartEntity c LEFT JOIN FETCH c.items i LEFT JOIN FETCH i.product WHERE c.user.id = :userId")
  Optional<CartEntity> findByUserId(@Param("userId") Long userId);

  boolean existsByUserId(Long userId);

  List<CartEntity> findByStatusAndLastActivityBefore(CartStatus status, LocalDateTime dateTime);
}
