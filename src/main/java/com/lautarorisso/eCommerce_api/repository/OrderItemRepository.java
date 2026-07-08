package com.lautarorisso.eCommerce_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lautarorisso.eCommerce_api.model.OrderItemEntity;

public interface OrderItemRepository extends JpaRepository<OrderItemEntity, Long> {
  boolean existsByProductId(Long productId);
}
