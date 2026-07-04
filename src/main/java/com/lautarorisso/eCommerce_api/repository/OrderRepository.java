package com.lautarorisso.eCommerce_api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lautarorisso.eCommerce_api.enums.OrderStatus;
import com.lautarorisso.eCommerce_api.model.OrderEntity;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
  Optional<OrderEntity> findById(Long orderId);

  List<OrderEntity> findByUserId(Long userId);

  List<OrderEntity> findByStatus(OrderStatus status);
}
