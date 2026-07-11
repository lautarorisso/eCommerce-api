package com.lautarorisso.eCommerce_api.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "order_items")
public class OrderItemEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id", nullable = false)
  private OrderEntity order;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id", nullable = false)
  private ProductEntity product;
  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal unitPrice;
  @Column(nullable = false)
  private int quantity;
  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal subtotal;

  public OrderItemEntity(OrderEntity order, ProductEntity product, BigDecimal unitPrice, int quantity) {
    if (order == null) {
      throw new IllegalArgumentException("Order cannot be null");
    }
    if (product == null) {
      throw new IllegalArgumentException("Product cannot be null");
    }
    if (unitPrice == null) {
      throw new IllegalArgumentException("Unit price cannot be null");
    }
    if (unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Unit price must be positive");
    }
    if (quantity <= 0) {
      throw new IllegalArgumentException("Quantity must be positive");
    }
    this.order = order;
    this.product = product;
    this.unitPrice = unitPrice;
    this.quantity = quantity;
    this.subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
  }
}
