package com.lautarorisso.eCommerce_api.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.lautarorisso.eCommerce_api.dto.response.orderDto;
import com.lautarorisso.eCommerce_api.mapper.orderMapper;
import com.lautarorisso.eCommerce_api.model.CartEntity;
import com.lautarorisso.eCommerce_api.model.CartItemEntity;
import com.lautarorisso.eCommerce_api.model.OrderEntity;
import com.lautarorisso.eCommerce_api.model.OrderItemEntity;
import com.lautarorisso.eCommerce_api.repository.CartRepository;
import com.lautarorisso.eCommerce_api.repository.OrderRepository;
import com.lautarorisso.eCommerce_api.service.OrderService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

  private final CartRepository cartRepository;
  private final OrderRepository orderRepository;
  private final orderMapper orderMapper;

  @Override
  public orderDto createOrder(Long cartId) {
    CartEntity cart = cartRepository.findById(cartId).orElseThrow(() -> new RuntimeException("Cart not found"));
    if (cart.isEmpty()) {
      throw new RuntimeException("Cart is empty");
    }
    OrderEntity order = new OrderEntity(cart.getUser());
    for (CartItemEntity cartItem : cart.getItems()) {
      OrderItemEntity orderItem = new OrderItemEntity(order, cartItem.getProduct().getName(), cartItem.getPrice(),
          cartItem.getQuantity());
      order.addItem(orderItem);
    }
    orderRepository.save(order);
    return orderMapper.toDto(order);
  }

  @Override
  public List<orderDto> getAllOrders(Long userId) {
    return orderRepository.findByUserId(userId).stream().map(orderMapper::toDto).toList();
  }

  @Override
  public orderDto getOrderById(Long orderId) {
    OrderEntity order = orderRepository.findById(orderId)
        .orElseThrow(() -> new RuntimeException("Order not found"));
    return orderMapper.toDto(order);
  }

  @Override
  public void cancelOrder(Long orderId) {
    OrderEntity order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
    order.cancel();
    orderRepository.save(order);
    orderMapper.toDto(order);
  }

  @Override
  public void payOrder(Long orderId) {
    OrderEntity order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
    order.markAsPaid();
    orderRepository.save(order);
    orderMapper.toDto(order);
  }

  @Override
  public void deliverOrder(Long orderId) {
    OrderEntity order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
    order.markAsDelivered();
    orderRepository.save(order);
    orderMapper.toDto(order);
  }

  @Override
  public void shipOrder(Long orderId) {
    OrderEntity order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
    order.markAsShipped();
    orderRepository.save(order);
    orderMapper.toDto(order);
  }

}
