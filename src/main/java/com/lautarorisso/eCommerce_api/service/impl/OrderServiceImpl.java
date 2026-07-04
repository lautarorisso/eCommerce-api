package com.lautarorisso.eCommerce_api.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.lautarorisso.eCommerce_api.dto.response.OrderDto;
import com.lautarorisso.eCommerce_api.mapper.OrderMapper;
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
  private final OrderMapper OrderMapper;

  @Override
  public OrderDto createOrder(Long cartId) {
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
    return OrderMapper.toDto(order);
  }

  @Override
  public List<OrderDto> getAllOrders(Long userId) {
    return orderRepository.findByUserId(userId).stream().map(OrderMapper::toDto).toList();
  }

  @Override
  public OrderDto getOrderById(Long orderId) {
    OrderEntity order = orderRepository.findById(orderId)
        .orElseThrow(() -> new RuntimeException("Order not found"));
    return OrderMapper.toDto(order);
  }

  @Override
  public void cancelOrder(Long orderId) {
    OrderEntity order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
    order.cancel();
    orderRepository.save(order);
    OrderMapper.toDto(order);
  }

  @Override
  public void payOrder(Long orderId) {
    OrderEntity order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
    order.markAsPaid();
    orderRepository.save(order);
    OrderMapper.toDto(order);
  }

  @Override
  public void deliverOrder(Long orderId) {
    OrderEntity order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
    order.markAsDelivered();
    orderRepository.save(order);
    OrderMapper.toDto(order);
  }

  @Override
  public void shipOrder(Long orderId) {
    OrderEntity order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
    order.markAsShipped();
    orderRepository.save(order);
    OrderMapper.toDto(order);
  }

}
