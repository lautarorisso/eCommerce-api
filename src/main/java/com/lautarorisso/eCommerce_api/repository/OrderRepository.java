package com.lautarorisso.eCommerce_api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.lautarorisso.eCommerce_api.model.OrderEntity;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
  boolean existsByUserId(Long userId);

  Page<OrderEntity> findByUserId(Long userId, Pageable pageable);

}
