package com.lautarorisso.eCommerce_api.model;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
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

  private Integer quantity;

  private BigDecimal price;

  public CartItemEntity(CartEntity cart, ProductEntity product, Integer quantity, BigDecimal price) {
    if (cart == null) {
      throw new IllegalArgumentException("Cart cannot be null");
    }
    if (product == null) {
      throw new IllegalArgumentException("Product cannot be null");
    }
    if (quantity <= 0) {
      throw new IllegalArgumentException("Quantity must be positive");
    }
    this.cart = cart;
    this.product = product;
    this.quantity = quantity;
    this.price = price;
  }

  public void changeQuantity(Integer newQuantity) {
    if (newQuantity <= 0) {
      throw new IllegalArgumentException("Quantity must be positive");
    }
    this.quantity = newQuantity;
  }

  public BigDecimal getSubtotal() {
    return this.price.multiply(BigDecimal.valueOf(this.quantity));
  }
}
