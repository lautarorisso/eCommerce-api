package com.lautarorisso.eCommerce_api.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lautarorisso.eCommerce_api.dto.response.OrderDto;
import com.lautarorisso.eCommerce_api.enums.OrderStatus;
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
import com.lautarorisso.eCommerce_api.security.SecurityUtils;
import com.lautarorisso.eCommerce_api.service.OrderService;
import com.lautarorisso.eCommerce_api.specification.OrderSpecification;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

  private final CartRepository cartRepository;
  private final OrderRepository orderRepository;
  private final ProductRepository productRepository;
  private final OrderMapper orderMapper;
  private final SecurityUtils securityUtils;

  @Transactional
  @Override
  public OrderDto createOrder(Long cartId) {
    CartEntity cart = cartRepository.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("Cart", cartId));
    if (!cart.isActive()) {
      throw new InvalidOperationException("Cart is not active");
    }
    securityUtils.assertOwnerOrAdmin(cart.getUser().getId());
    if (cart.isEmpty()) {
      throw new InvalidOperationException("Cart is empty");
    }
    OrderEntity order = new OrderEntity(cart.getUser());
    for (CartItemEntity cartItem : cart.getItems()) {
      ProductEntity product = cartItem.getProduct();
      if (product.getStock() < cartItem.getQuantity()) {
        throw new InsufficientResourcesException(product.getName());
      }
      OrderItemEntity orderItem = new OrderItemEntity(order, cartItem.getProduct(), cartItem.getQuantity(),
          cartItem.getUnitPrice());
      order.addItem(orderItem);
    }
    cart.clear();
    orderRepository.save(order);
    return orderMapper.toDto(order);
  }

  @Transactional(readOnly = true)
  @Override
  public Page<OrderDto> getAllOrders(Long userId, OrderStatus status, BigDecimal minTotal, BigDecimal maxTotal,
      Pageable pageable) {
    securityUtils.assertOwnerOrAdmin(userId);
    Specification<OrderEntity> spec = Specification
        .where(OrderSpecification.userIdEquals(userId))
        .and(OrderSpecification.statusEquals(status))
        .and(OrderSpecification.totalBetween(minTotal, maxTotal));
    return orderRepository.findAll(spec, pageable).map(orderMapper::toDto);
  }

  @Transactional(readOnly = true)
  @Override
  public OrderDto getOrderById(Long orderId) {
    OrderEntity order = orderRepository.findById(orderId)
        .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));
    securityUtils.assertOwnerOrAdmin(order.getUser().getId());
    return orderMapper.toDto(order);
  }

  @Transactional
  @Override
  public void cancelOrder(Long orderId) {
    OrderEntity order = orderRepository.findById(orderId)
        .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));
    securityUtils.assertOwnerOrAdmin(order.getUser().getId());
    order.cancel();
    orderRepository.save(order);
  }

  @Transactional
  @Override
  public void payOrder(Long orderId) {
    OrderEntity order = orderRepository.findById(orderId)
        .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));
    securityUtils.assertOwnerOrAdmin(order.getUser().getId());
    List<ProductEntity> products = order.getItems().stream()
        .map(item -> {
          try {
            item.getProduct().decreaseStock(item.getQuantity());
          } catch (IllegalArgumentException e) {
            throw new InsufficientResourcesException(item.getProduct().getName());
          }
          return item.getProduct();
        })
        .toList();
    order.markAsPaid();
    productRepository.saveAll(products);
    orderRepository.save(order);
  }

  @Transactional
  @Override
  public void shipOrder(Long orderId) {
    OrderEntity order = orderRepository.findById(orderId)
        .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));
    securityUtils.assertAdmin();
    order.markAsShipped();
    orderRepository.save(order);
  }

  @Transactional
  @Override
  public void deliverOrder(Long orderId) {
    OrderEntity order = orderRepository.findById(orderId)
        .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));
    securityUtils.assertAdmin();
    order.markAsDelivered();
    orderRepository.save(order);
  }
}
