package com.lautarorisso.eCommerce_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lautarorisso.eCommerce_api.model.CategoryEntity;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
  boolean existsByName(String name);
}
