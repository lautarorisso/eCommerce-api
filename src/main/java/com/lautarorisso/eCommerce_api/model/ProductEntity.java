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
import jakarta.persistence.Version;
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
  private BigDecimal unitPrice;
  @Column(nullable = false)
  private Integer stock;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id")
  private CategoryEntity category;

  @Version
  private Long version;

  public ProductEntity(String name, String description, BigDecimal unitPrice, Integer stock) {
    this.name = name;
    this.description = description;
    this.unitPrice = unitPrice;
    this.stock = stock;
  }

  public void changeName(String name) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Name cannot be empty");
    }
    this.name = name;
  }

  public void changeDescription(String description) {
    if (description == null || description.isBlank()) {
      throw new IllegalArgumentException("Description cannot be empty");
    }
    this.description = description;
  }

  public void changeCategory(CategoryEntity category) {
    this.category = category;
  }

  public void updatePrice(BigDecimal newPrice) {
    if (newPrice == null) {
      throw new IllegalArgumentException("Price cannot be null");
    }
    if (newPrice.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("Price cannot be negative");
    }
    this.unitPrice = newPrice;
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
