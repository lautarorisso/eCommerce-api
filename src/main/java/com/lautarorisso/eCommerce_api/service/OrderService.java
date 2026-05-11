package com.lautarorisso.eCommerce_api.service;

import java.util.List;

import com.lautarorisso.eCommerce_api.model.OrderEntity;

public interface OrderService {
    OrderEntity createOrder(OrderEntity order);

    List<OrderEntity> getAllOrders();

    OrderEntity getOrderById(Long id);

    void cancelOrder(Long id);
}