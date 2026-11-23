package com.lautarorisso.eCommerce_api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lautarorisso.eCommerce_api.model.CategoryEntity;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
  boolean existsByName(String name);

  @Query(value = "SELECT * FROM categories WHERE id = :id AND deleted_at IS NOT NULL", nativeQuery = true)
  Optional<CategoryEntity> findDeletedById(@Param("id") Long id);
}
