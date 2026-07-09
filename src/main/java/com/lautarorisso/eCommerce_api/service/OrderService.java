package com.lautarorisso.eCommerce_api.service;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.lautarorisso.eCommerce_api.dto.response.OrderDto;
import com.lautarorisso.eCommerce_api.enums.OrderStatus;

public interface OrderService {
  OrderDto createOrder(Long cartId);

  Page<OrderDto> getAllOrders(Long userId, OrderStatus status, BigDecimal minTotal, BigDecimal maxTotal,
      Pageable pageable);

  OrderDto getOrderById(Long orderId);

  void cancelOrder(Long orderId);

  void payOrder(Long orderId);

  void shipOrder(Long orderId);

  void deliverOrder(Long orderId);

}
