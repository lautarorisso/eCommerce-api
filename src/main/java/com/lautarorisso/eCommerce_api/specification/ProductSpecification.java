package com.lautarorisso.eCommerce_api.specification;

import java.math.BigDecimal;

import org.springframework.data.jpa.domain.Specification;

import com.lautarorisso.eCommerce_api.model.ProductEntity;

public class ProductSpecification {

  public static Specification<ProductEntity> nameContains(String search) {
    return (root, query, cb) -> {
      if (search == null || search.isBlank()) {
        return null;
      }
      return cb.like(cb.lower(root.get("name")), "%" + search.toLowerCase() + "%");
    };
  }

  public static Specification<ProductEntity> priceBetween(BigDecimal min, BigDecimal max) {
    return (root, query, cb) -> {
      if (min == null && max == null) {
        return null;
      }
      if (min == null) {
        return cb.lessThanOrEqualTo(root.get("unitPrice"), max);
      }
      if (max == null) {
        return cb.greaterThanOrEqualTo(root.get("unitPrice"), min);
      }
      return cb.between(root.get("unitPrice"), min, max);
    };
  }

  public static Specification<ProductEntity> inStock(Boolean inStock) {
    return (root, query, cb) -> {
      if (inStock == null)
        return null;
      if (inStock)
        return cb.greaterThan(root.get("stock"), 0);
      return cb.equal(root.get("stock"), 0);
    };
  }
}
