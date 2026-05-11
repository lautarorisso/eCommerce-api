package com.lautarorisso.eCommerce_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lautarorisso.eCommerce_api.model.OrderEntity;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
}