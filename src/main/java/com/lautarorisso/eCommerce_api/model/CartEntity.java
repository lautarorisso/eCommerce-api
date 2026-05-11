package com.lautarorisso.eCommerce_api.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class CartEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @OneToOne
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity user;
  @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<CartItemEntity> items = new ArrayList<>();

  public CartEntity(UserEntity user) {
    if (user == null) {
      throw new IllegalArgumentException("User cannot be null");
    }
    this.user = user;
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
}
