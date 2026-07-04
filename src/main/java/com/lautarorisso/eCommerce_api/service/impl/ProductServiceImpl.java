package com.lautarorisso.eCommerce_api.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.lautarorisso.eCommerce_api.dto.request.CreateProductRequest;
import com.lautarorisso.eCommerce_api.dto.request.UpdateProductRequest;
import com.lautarorisso.eCommerce_api.dto.response.ProductDto;
import com.lautarorisso.eCommerce_api.mapper.ProductMapper;
import com.lautarorisso.eCommerce_api.model.ProductEntity;
import com.lautarorisso.eCommerce_api.repository.ProductRepository;
import com.lautarorisso.eCommerce_api.service.ProductService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

  private final ProductMapper ProductMapper;
  private final ProductRepository productRepository;

  @Override
  public ProductDto createProduct(CreateProductRequest request) {
    if (productRepository.existsByName(request.name())) {
      throw new RuntimeException("Product already exists");
    }
    ProductEntity product = ProductMapper.toEntity(request);
    ProductEntity savedProduct = productRepository.save(product);
    return ProductMapper.toDto(savedProduct);
  }

  @Override
  public ProductDto getProductById(Long productId) {
    ProductEntity product = productRepository.findById(productId)
        .orElseThrow(() -> new RuntimeException("Product not found"));
    return ProductMapper.toDto(product);
  }

  @Override
  public List<ProductDto> getAllProducts() {
    return productRepository.findAll().stream().map(ProductMapper::toDto).toList();
  }

  @Override
  public ProductDto updateProduct(Long productId, UpdateProductRequest request) {
    ProductEntity product = productRepository.findById(productId)
        .orElseThrow(() -> new RuntimeException("Product not found"));
    if (request.name() != null) {
      product.changeName(request.name());
    }
    if (request.description() != null) {
      product.changeDescription(request.description());
    }
    if (request.price() != null) {
      product.updatePrice(request.price());
    }
    ProductEntity updatedProduct = productRepository.save(product);
    return ProductMapper.toDto(updatedProduct);
  }

  @Override
  public void deleteProduct(Long productId) {
    if (!productRepository.existsById(productId)) {
      throw new RuntimeException("Product not found");
    }
    productRepository.deleteById(productId);
  }

  @Override
  public void restockProduct(Long productId, int quantity) {
    ProductEntity product = productRepository.findById(productId)
        .orElseThrow(() -> new RuntimeException("Product not found"));
    product.restock(quantity);
    productRepository.save(product);
  }
}
