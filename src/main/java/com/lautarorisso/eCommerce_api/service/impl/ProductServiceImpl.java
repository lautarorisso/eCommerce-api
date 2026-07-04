package com.lautarorisso.eCommerce_api.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.lautarorisso.eCommerce_api.dto.request.CreateProductRequest;
import com.lautarorisso.eCommerce_api.dto.request.UpdateProductRequest;
import com.lautarorisso.eCommerce_api.dto.response.productDto;
import com.lautarorisso.eCommerce_api.mapper.productMapper;
import com.lautarorisso.eCommerce_api.model.ProductEntity;
import com.lautarorisso.eCommerce_api.repository.ProductRepository;
import com.lautarorisso.eCommerce_api.service.ProductService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

  private final productMapper productMapper;
  private final ProductRepository productRepository;

  @Override
  public productDto createProduct(CreateProductRequest request) {
    if (productRepository.existsByName(request.name())) {
      throw new RuntimeException("Product already exists");
    }
    ProductEntity product = productMapper.toEntity(request);
    ProductEntity savedProduct = productRepository.save(product);
    return productMapper.toDto(savedProduct);
  }

  @Override
  public productDto getProductById(Long productId) {
    ProductEntity product = productRepository.findById(productId)
        .orElseThrow(() -> new RuntimeException("Product not found"));
    return productMapper.toDto(product);
  }

  @Override
  public List<productDto> getAllProducts() {
    return productRepository.findAll().stream().map(productMapper::toDto).toList();
  }

  @Override
  public productDto updateProduct(Long productId, UpdateProductRequest request) {
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
    return productMapper.toDto(updatedProduct);
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
