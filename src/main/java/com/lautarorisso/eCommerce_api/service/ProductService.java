package com.lautarorisso.eCommerce_api.service;

import java.util.List;

import com.lautarorisso.eCommerce_api.model.ProductEntity;

public interface ProductService {
  ProductDto createProduct(CreateProductRequest request);

  ProductDto getProductById(Long productId);

  List<ProductDto> getAllProducts();

  ProductDto updateProduct(Long productId, UpdateProductRequest request);

  void deleteProduct(Long productId);

  void restockProduct(Long productId, int Quantity);

}
