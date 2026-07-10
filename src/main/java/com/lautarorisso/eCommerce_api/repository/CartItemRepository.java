package com.lautarorisso.eCommerce_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lautarorisso.eCommerce_api.model.CartItemEntity;

public interface CartItemRepository extends JpaRepository<CartItemEntity, Long> {
  @Query("SELECT COUNT(ci) > 0 FROM CartItemEntity ci WHERE ci.product.id = :productId AND ci.cart.status = 'ACTIVE'")
  boolean existsByProductIdAndActiveCart(@Param("productId") Long productId);
}
