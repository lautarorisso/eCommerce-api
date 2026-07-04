package com.lautarorisso.eCommerce_api.service;

import java.util.List;

import com.lautarorisso.eCommerce_api.dto.request.CreateProductRequest;
import com.lautarorisso.eCommerce_api.dto.request.UpdateProductRequest;
import com.lautarorisso.eCommerce_api.dto.response.productDto;

public interface ProductService {
  productDto createProduct(CreateProductRequest request);

  productDto getProductById(Long productId);

  List<productDto> getAllProducts();

  productDto updateProduct(Long productId, UpdateProductRequest request);

  void deleteProduct(Long productId);

  void restockProduct(Long productId, int Quantity);

}
