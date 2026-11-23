package com.lautarorisso.eCommerce_api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lautarorisso.eCommerce_api.model.ProductEntity;

public interface ProductRepository extends JpaRepository<ProductEntity, Long>, JpaSpecificationExecutor<ProductEntity> {
  boolean existsByName(String name);

  boolean existsByCategoryId(Long categoryId);

  @Query(value = "SELECT * FROM products WHERE id = :id AND deleted_at IS NOT NULL", nativeQuery = true)
  Optional<ProductEntity> findDeletedById(@Param("id") Long id);
}
