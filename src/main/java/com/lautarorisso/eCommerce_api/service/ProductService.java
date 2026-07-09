package com.lautarorisso.eCommerce_api.service;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.lautarorisso.eCommerce_api.dto.request.CreateProductRequest;
import com.lautarorisso.eCommerce_api.dto.request.UpdateProductRequest;
import com.lautarorisso.eCommerce_api.dto.response.ProductDto;

public interface ProductService {
  ProductDto createProduct(CreateProductRequest request);

  ProductDto getProductById(Long productId);

  Page<ProductDto> getAllProducts(String search, BigDecimal minPrice, BigDecimal maxPrice, Boolean inStock,
      Pageable pageable);

  ProductDto updateProduct(Long productId, UpdateProductRequest request);

  void deleteProduct(Long productId);

  void restockProduct(Long productId, int quantity);

}
