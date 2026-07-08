package com.lautarorisso.eCommerce_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lautarorisso.eCommerce_api.model.CartItemEntity;

public interface CartItemRepository extends JpaRepository<CartItemEntity, Long> {
  boolean existsByProductId(Long productId);
}
