package com.lautarorisso.eCommerce_api.model;

import java.math.BigDecimal;

import com.lautarorisso.eCommerce_api.exceptions.InsufficientResourcesException;

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
@Table(name = "cart_items")
public class CartItemEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "cart_id", nullable = false)
  private CartEntity cart;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id", nullable = false)
  private ProductEntity product;
  @Column(nullable = false)
  private int quantity;
  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal unitPrice;

  public CartItemEntity(CartEntity cart, ProductEntity product, int quantity, BigDecimal unitPrice) {
    if (cart == null) {
      throw new IllegalArgumentException("Cart cannot be null");
    }
    if (product == null) {
      throw new IllegalArgumentException("Product cannot be null");
    }
    if (quantity <= 0) {
      throw new IllegalArgumentException("Quantity must be positive");
    }
    if (unitPrice == null) {
      throw new IllegalArgumentException("Unit price cannot be null");
    }
    if (unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Unit price must be positive");
    }
    this.cart = cart;
    this.product = product;
    this.quantity = quantity;
    this.unitPrice = unitPrice;
  }

  public void changeQuantity(int newQuantity) {
    if (newQuantity <= 0) {
      throw new IllegalArgumentException("Quantity must be positive");
    }
    if (newQuantity > this.product.getStock()) {
      throw new InsufficientResourcesException(this.product.getName());
    }
    this.quantity = newQuantity;
  }

  public BigDecimal getSubtotal() {
    return this.unitPrice.multiply(BigDecimal.valueOf(this.quantity));
  }
}
