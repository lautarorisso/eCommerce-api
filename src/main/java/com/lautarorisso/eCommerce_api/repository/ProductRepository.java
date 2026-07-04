package com.lautarorisso.eCommerce_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lautarorisso.eCommerce_api.model.ProductEntity;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
  List<ProductEntity> findByCategory(String category);

  List<ProductEntity> findByNameContainingIgnoreCase(String name);

  boolean existsByName(String name);
}
