package com.lautarorisso.eCommerce_api.specification;

import java.math.BigDecimal;

import org.springframework.data.jpa.domain.Specification;

import com.lautarorisso.eCommerce_api.enums.OrderStatus;
import com.lautarorisso.eCommerce_api.model.OrderEntity;

public class OrderSpecification {

  public static Specification<OrderEntity> userIdEquals(Long userId) {
    return (root, query, cb) -> {
      if (userId == null) {
        return null;
      }
      return cb.equal(root.get("user").get("id"), userId);
    };
  }

  public static Specification<OrderEntity> statusEquals(OrderStatus status) {
    return (root, query, cb) -> {
      if (status == null) {
        return null;
      }
      return cb.equal(root.get("status"), status);
    };
  }

  public static Specification<OrderEntity> totalBetween(BigDecimal min, BigDecimal max) {
    return (root, query, cb) -> {
      if (min == null && max == null) {
        return null;
      }
      if (min == null) {
        return cb.lessThanOrEqualTo(root.get("totalPrice"), max);
      }
      if (max == null) {
        return cb.greaterThanOrEqualTo(root.get("totalPrice"), min);
      }
      return cb.between(root.get("totalPrice"), min, max);
    };
  }
}
