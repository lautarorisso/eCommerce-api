package com.lautarorisso.eCommerce_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lautarorisso.eCommerce_api.model.ProductEntity;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
}