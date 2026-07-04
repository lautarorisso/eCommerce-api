package com.lautarorisso.eCommerce_api.service;

import java.util.List;

import com.lautarorisso.eCommerce_api.dto.response.orderDto;

public interface OrderService {
  orderDto createOrder(Long cartId);

  List<orderDto> getAllOrders(Long userId);

  orderDto getOrderById(Long orderId);

  void cancelOrder(Long orderId);

  void payOrder(Long orderId);

  void shipOrder(Long orderId);

  void deliverOrder(Long orderId);

}
