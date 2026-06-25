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
  @Column(nullable = false)
  private String productName;
  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal unitPrice;
  @Column(nullable = false)
  private Integer quantity;
  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal subtotal;

  public OrderItemEntity(OrderEntity order, String productName, BigDecimal unitPrice, Integer quantity) {
    this.order = order;
    this.productName = productName;
    this.unitPrice = unitPrice;
    this.quantity = quantity;
    this.subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
  }
}
