package com.lautarorisso.eCommerce_api.service;

import java.util.List;

import com.lautarorisso.eCommerce_api.model.ProductEntity;

public interface ProductService {
    ProductEntity createProduct(ProductEntity product);

    List<ProductEntity> getAllProducts();

    ProductEntity updateProduct(Long id, ProductEntity product);

    void deleteProduct(Long id);

    ProductEntity getProductById(Long id);
}
