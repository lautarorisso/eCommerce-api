package com.lautarorisso.eCommerce_api.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "products")
public class ProductEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(nullable = false)
  private String name;
  @Column(nullable = false, length = 500)
  private String description;
  @Column(nullable = false, precision = 12, scale = 2)
  private BigDecimal price;
  @Column(nullable = false)
  private int stock;

  public ProductEntity(String name, String description, BigDecimal price, int stock) {
    this.name = name;
    this.description = description;
    this.price = price;
    this.stock = stock;
  }

  public void updatePrice(BigDecimal newPrice) {
    if (newPrice == null) {
      throw new IllegalArgumentException("Price cannot be null");
    }
    if (newPrice.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("Price cannot be negative");
    }
    this.price = newPrice;
  }

  public void restock(int quantity) {
    if (quantity <= 0) {
      throw new IllegalArgumentException("Quantity must be positive");
    }
    this.stock += quantity;
  }

  public void decreaseStock(int quantity) {
    if (quantity <= 0) {
      throw new IllegalArgumentException("Quantity must be positive");
    }
    if (this.stock < quantity) {
      throw new IllegalArgumentException("Not enough stock");
    }
    this.stock -= quantity;
  }
}
