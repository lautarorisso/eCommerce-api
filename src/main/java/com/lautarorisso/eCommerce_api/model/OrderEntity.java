package com.lautarorisso.eCommerce_api.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.lautarorisso.eCommerce_api.enums.OrderStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "orders")
public class OrderEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity user;
  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<OrderItemEntity> items;
  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal totalPrice;
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private OrderStatus status;
  @Column(nullable = false)
  private LocalDateTime createdAt;

  public OrderEntity(UserEntity user, List<OrderItemEntity> items, BigDecimal totalPrice) {
    this.user = user;
    this.items = items;
    this.totalPrice = calculateTotalPrice();
    this.status = OrderStatus.PENDING;
    this.createdAt = LocalDateTime.now();
  }

  private BigDecimal calculateTotalPrice() {
    return items.stream().map(OrderItemEntity::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  public void markAsPaid() {
    if (status != OrderStatus.PENDING) {
      throw new IllegalStateException("Only pending orders can be paid");
    }
    this.status = OrderStatus.PAID;
  }

  public void markAsShipped() {
    if (status != OrderStatus.PAID) {
      throw new IllegalStateException("Only paid orders can be shipped");
    }
    this.status = OrderStatus.SHIPPED;
  }

  public void markAsDelivered() {
    if (status != OrderStatus.SHIPPED) {
      throw new IllegalStateException("Only shipped orders can be delivered");
    }
    this.status = OrderStatus.DELIVERED;
  }

  public void cancel() {
    if (this.status == OrderStatus.DELIVERED) {
      throw new IllegalStateException("delivered orders cannot be cancelled");
    }
    this.status = OrderStatus.CANCELLED;
  }

}
