package com.lautarorisso.eCommerce_api.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.lautarorisso.eCommerce_api.enums.CartStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "carts")
public class CartEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Version
  private Long version;
  @OneToOne
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity user;
  @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<CartItemEntity> items = new ArrayList<>();
  @Enumerated(EnumType.STRING)
  private CartStatus status;
  @Column(nullable = false)
  private LocalDateTime lastActivity;

  public CartEntity(UserEntity user) {
    if (user == null) {
      throw new IllegalArgumentException("User cannot be null");
    }
    this.user = user;
    this.items = new ArrayList<>();
    this.status = CartStatus.ACTIVE;
    this.lastActivity = LocalDateTime.now();
  }

  @PrePersist
  @PreUpdate
  public void updateLastActivity() {
    this.lastActivity = LocalDateTime.now();
  }

  public void addItem(CartItemEntity item) {
    if (item == null) {
      throw new IllegalArgumentException("Item cannot be null");
    }
    this.items.add(item);
  }

  public void removeItem(CartItemEntity item) {
    this.items.remove(item);
  }

  public void clear() {
    this.items.clear();
  }

  public boolean isEmpty() {
    return this.items.isEmpty();
  }

  public BigDecimal calculateTotal() {
    return items.stream().map(CartItemEntity::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  public boolean isActive() {
    return this.status == CartStatus.ACTIVE;
  }

  public void abandon() {
    this.status = CartStatus.ABANDONED;
  }

}
