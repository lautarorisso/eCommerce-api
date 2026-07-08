package com.lautarorisso.eCommerce_api.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lautarorisso.eCommerce_api.dto.response.OrderDto;
import com.lautarorisso.eCommerce_api.exceptions.InsufficientResourcesException;
import com.lautarorisso.eCommerce_api.exceptions.InvalidOperationException;
import com.lautarorisso.eCommerce_api.exceptions.ResourceNotFoundException;
import com.lautarorisso.eCommerce_api.mapper.OrderMapper;
import com.lautarorisso.eCommerce_api.model.CartEntity;
import com.lautarorisso.eCommerce_api.model.CartItemEntity;
import com.lautarorisso.eCommerce_api.model.OrderEntity;
import com.lautarorisso.eCommerce_api.model.OrderItemEntity;
import com.lautarorisso.eCommerce_api.model.ProductEntity;
import com.lautarorisso.eCommerce_api.repository.CartRepository;
import com.lautarorisso.eCommerce_api.repository.OrderRepository;
import com.lautarorisso.eCommerce_api.repository.ProductRepository;
import com.lautarorisso.eCommerce_api.service.OrderService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

  private final CartRepository cartRepository;
  private final OrderRepository orderRepository;
  private final ProductRepository productRepository;
  private final OrderMapper orderMapper;

  @Transactional
  @Override
  public OrderDto createOrder(Long cartId) {
    CartEntity cart = cartRepository.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("Cart", cartId));
    if (cart.isEmpty()) {
      throw new InvalidOperationException("Cart is empty");
    }
    OrderEntity order = new OrderEntity(cart.getUser());
    for (CartItemEntity cartItem : cart.getItems()) {
      ProductEntity product = cartItem.getProduct();
      if (product.getStock() < cartItem.getQuantity()) {
        throw new InsufficientResourcesException(product.getName(), cartItem.getQuantity(), product.getStock());
      }
      OrderItemEntity orderItem = new OrderItemEntity(order, cartItem.getProduct(), cartItem.getUnitPrice(),
          cartItem.getQuantity());
      order.addItem(orderItem);
    }
    cart.clear();
    orderRepository.save(order);
    return orderMapper.toDto(order);
  }

  @Override
  public List<OrderDto> getAllOrders(Long userId) {
    return orderRepository.findByUserId(userId).stream().map(orderMapper::toDto).toList();
  }

  @Override
  public OrderDto getOrderById(Long orderId) {
    OrderEntity order = orderRepository.findById(orderId)
        .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));
    return orderMapper.toDto(order);
  }

  @Transactional
  @Override
  public void cancelOrder(Long orderId) {
    OrderEntity order = orderRepository.findById(orderId)
        .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));
    order.cancel();
    List<ProductEntity> products = order.getItems().stream()
        .map(item -> {
          item.getProduct().restock(item.getQuantity());
          return item.getProduct();
        })
        .toList();
    productRepository.saveAll(products);
    orderRepository.save(order);
  }

  @Transactional
  @Override
  public void payOrder(Long orderId) {
    OrderEntity order = orderRepository.findById(orderId)
        .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));
    order.markAsPaid();
    List<ProductEntity> products = order.getItems().stream()
        .map(item -> {
          item.getProduct().decreaseStock(item.getQuantity());
          return item.getProduct();
        })
        .toList();
    productRepository.saveAll(products);
    orderRepository.save(order);
  }

  @Transactional
  @Override
  public void deliverOrder(Long orderId) {
    OrderEntity order = orderRepository.findById(orderId)
        .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));
    order.markAsDelivered();
    orderRepository.save(order);
  }

  @Transactional
  @Override
  public void shipOrder(Long orderId) {
    OrderEntity order = orderRepository.findById(orderId)
        .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));
    order.markAsShipped();
    orderRepository.save(order);
  }
}
