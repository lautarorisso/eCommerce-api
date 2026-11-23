package com.lautarorisso.eCommerce_api.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
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
    List<ProductEntity> orderedProducts = new ArrayList<>();
    for (CartItemEntity cartItem : cart.getItems()) {
      ProductEntity product = cartItem.getProduct();
      // Validate price hasn't changed since item was added to cart
      if (cartItem.getUnitPrice().compareTo(product.getUnitPrice()) != 0) {
        throw new InvalidOperationException(
            "Price for product '" + product.getName() + "' has changed. Please update your cart.");
      }
      if (product.getStock() < cartItem.getQuantity()) {
        throw new InsufficientResourcesException(product.getName());
      }
      try {
        product.decreaseStock(cartItem.getQuantity());
      } catch (IllegalArgumentException e) {
        throw new InsufficientResourcesException(product.getName());
      }
      OrderItemEntity orderItem = new OrderItemEntity(order, cartItem.getProduct(), cartItem.getQuantity(),
          cartItem.getUnitPrice());
      order.addItem(orderItem);
      orderedProducts.add(product);
    }
    cart.clear();
    orderRepository.save(order);
    productRepository.saveAll(orderedProducts);
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
    // Restore stock since it was decremented at order creation
    List<OrderItemEntity> items = order.getItems();
    if (items != null && !items.isEmpty()) {
      List<ProductEntity> productsToRestore = new ArrayList<>();
      for (OrderItemEntity item : items) {
        ProductEntity product = item.getProduct();
        try {
          product.increaseStock(item.getQuantity());
        } catch (IllegalArgumentException e) {
          // Ignore if product no longer exists
        }
        productsToRestore.add(product);
      }
      productRepository.saveAll(productsToRestore);
    }
    orderRepository.save(order);
  }

  @Transactional
  @Override
  public void payOrder(Long orderId) {
    OrderEntity order = orderRepository.findById(orderId)
        .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));
    securityUtils.assertOwnerOrAdmin(order.getUser().getId());
    // Stock was already decremented at order creation; no need to decrement again
    order.markAsPaid();
    orderRepository.save(order);
  }

  @Transactional
  @Override
  public void shipOrder(Long orderId, String shippingAddress, String trackingNumber) {
    OrderEntity order = orderRepository.findById(orderId)
        .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));
    securityUtils.assertAdmin();
    order.markAsShipped(shippingAddress, trackingNumber);
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
