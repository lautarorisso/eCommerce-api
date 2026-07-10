package com.lautarorisso.eCommerce_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.lautarorisso.eCommerce_api.model.ProductEntity;

public interface ProductRepository extends JpaRepository<ProductEntity, Long>, JpaSpecificationExecutor<ProductEntity> {
  boolean existsByName(String name);

  boolean existsByCategoryId(Long categoryId);
}
