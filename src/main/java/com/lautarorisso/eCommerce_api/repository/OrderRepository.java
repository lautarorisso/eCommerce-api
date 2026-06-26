package com.lautarorisso.eCommerce_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lautarorisso.eCommerce_api.enums.OrderStatus;
import com.lautarorisso.eCommerce_api.model.OrderEntity;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
  List<OrderEntity> findByUserId(Long userId);

  List<OrderEntity> findByStatus(OrderStatus status);
}
