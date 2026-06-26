package com.lautarorisso.eCommerce_api.service;

import java.util.List;

import com.lautarorisso.eCommerce_api.model.OrderEntity;

public interface OrderService {
  OrderDto createOrder(Long userId);

  List<OrderDto> getAllOrders(Long userId);

  OrderDto getOrderById(Long orderId);

  void cancelOrder(Long orderId);

  void payOrder(Long orderId);

  void shipOrder(Long orderId);

  void deliverOrder(Long orderId);

}
