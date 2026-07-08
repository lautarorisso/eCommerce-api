package com.lautarorisso.eCommerce_api.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.lautarorisso.eCommerce_api.dto.request.CreateProductRequest;
import com.lautarorisso.eCommerce_api.dto.request.UpdateProductRequest;
import com.lautarorisso.eCommerce_api.dto.response.ProductDto;
import com.lautarorisso.eCommerce_api.exceptions.DuplicateResourceException;
import com.lautarorisso.eCommerce_api.exceptions.InvalidOperationException;
import com.lautarorisso.eCommerce_api.exceptions.ResourceNotFoundException;
import com.lautarorisso.eCommerce_api.mapper.ProductMapper;
import com.lautarorisso.eCommerce_api.model.ProductEntity;
import com.lautarorisso.eCommerce_api.repository.CartItemRepository;
import com.lautarorisso.eCommerce_api.repository.OrderItemRepository;
import com.lautarorisso.eCommerce_api.repository.ProductRepository;
import com.lautarorisso.eCommerce_api.service.ProductService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

  private final ProductMapper productMapper;
  private final ProductRepository productRepository;
  private final CartItemRepository cartItemRepository;
  private final OrderItemRepository orderItemRepository;

  @Override
  public ProductDto createProduct(CreateProductRequest request) {
    if (productRepository.existsByName(request.name())) {
      throw new DuplicateResourceException("Product", "name", request.name());
    }
    ProductEntity product = productMapper.toEntity(request);
    ProductEntity savedProduct = productRepository.save(product);
    return productMapper.toDto(savedProduct);
  }

  @Override
  public ProductDto getProductById(Long productId) {
    ProductEntity product = productRepository.findById(productId)
        .orElseThrow(() -> new ResourceNotFoundException("Product", productId));
    return productMapper.toDto(product);
  }

  @Override
  public List<ProductDto> getAllProducts() {
    return productRepository.findAll().stream().map(productMapper::toDto).toList();
  }

  @Override
  public ProductDto updateProduct(Long productId, UpdateProductRequest request) {
    ProductEntity product = productRepository.findById(productId)
        .orElseThrow(() -> new ResourceNotFoundException("Product", productId));
    if (request.name() != null) {
      product.changeName(request.name());
    }
    if (request.description() != null) {
      product.changeDescription(request.description());
    }
    if (request.unitPrice() != null) {
      product.updatePrice(request.unitPrice());
    }
    ProductEntity updatedProduct = productRepository.save(product);
    return productMapper.toDto(updatedProduct);
  }

  @Override
  public void deleteProduct(Long productId) {
    if (!productRepository.existsById(productId)) {
      throw new ResourceNotFoundException("Product", productId);
    }
    if (cartItemRepository.existsByProductId(productId)) {
      throw new InvalidOperationException("Cannot delete product: it is in one or more carts");
    }
    if (orderItemRepository.existsByProductId(productId)) {
      throw new InvalidOperationException("Cannot delete product: it is in one or more orders");
    }
    productRepository.deleteById(productId);
  }

  @Override
  public void restockProduct(Long productId, int quantity) {
    ProductEntity product = productRepository.findById(productId)
        .orElseThrow(() -> new ResourceNotFoundException("Product", productId));
    product.restock(quantity);
    productRepository.save(product);
  }
}
