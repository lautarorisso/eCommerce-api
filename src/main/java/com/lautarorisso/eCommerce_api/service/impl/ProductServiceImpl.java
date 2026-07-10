package com.lautarorisso.eCommerce_api.service.impl;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.lautarorisso.eCommerce_api.dto.request.CreateProductRequest;
import com.lautarorisso.eCommerce_api.dto.request.UpdateProductRequest;
import com.lautarorisso.eCommerce_api.dto.response.ProductDto;
import com.lautarorisso.eCommerce_api.exceptions.DuplicateResourceException;
import com.lautarorisso.eCommerce_api.exceptions.InvalidOperationException;
import com.lautarorisso.eCommerce_api.exceptions.ResourceNotFoundException;
import com.lautarorisso.eCommerce_api.mapper.ProductMapper;
import com.lautarorisso.eCommerce_api.model.CategoryEntity;
import com.lautarorisso.eCommerce_api.model.ProductEntity;
import com.lautarorisso.eCommerce_api.repository.CartItemRepository;
import com.lautarorisso.eCommerce_api.repository.CategoryRepository;
import com.lautarorisso.eCommerce_api.repository.OrderItemRepository;
import com.lautarorisso.eCommerce_api.repository.ProductRepository;
import com.lautarorisso.eCommerce_api.service.ProductService;
import com.lautarorisso.eCommerce_api.specification.ProductSpecification;

import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

  private final ProductMapper productMapper;
  private final ProductRepository productRepository;
  private final CartItemRepository cartItemRepository;
  private final OrderItemRepository orderItemRepository;
  private final CategoryRepository categoryRepository;

  @Transactional
  @Override
  public ProductDto createProduct(CreateProductRequest request) {
    if (productRepository.existsByName(request.name())) {
      throw new DuplicateResourceException("Product", "name", request.name());
    }
    CategoryEntity category = categoryRepository.findById(request.categoryId())
        .orElseThrow(() -> new ResourceNotFoundException("Category", request.categoryId()));
    ProductEntity product = productMapper.toEntity(request);
    product.changeCategory(category);
    ProductEntity savedProduct = productRepository.save(product);
    return productMapper.toDto(savedProduct);
  }

  @Transactional(readOnly = true)
  @Override
  public ProductDto getProductById(Long productId) {
    ProductEntity product = productRepository.findById(productId)
        .orElseThrow(() -> new ResourceNotFoundException("Product", productId));
    return productMapper.toDto(product);
  }

  @Transactional(readOnly = true)
  @Override
  public Page<ProductDto> getAllProducts(String search, BigDecimal minPrice, BigDecimal maxPrice, Boolean inStock,
      Long categoryId, Pageable pageable) {
    Specification<ProductEntity> spec = Specification
        .where(ProductSpecification.nameContains(search))
        .and(ProductSpecification.priceBetween(minPrice, maxPrice))
        .and(ProductSpecification.inStock(inStock))
        .and(ProductSpecification.categoryIdEquals(categoryId));
    return productRepository.findAll(spec, pageable).map(productMapper::toDto);
  }

  @Transactional
  @Override
  public ProductDto updateProduct(Long productId, UpdateProductRequest request) {
    ProductEntity product = productRepository.findById(productId)
        .orElseThrow(() -> new ResourceNotFoundException("Product", productId));
    if (request.name() != null && !request.name().equals(product.getName())) {
      if (productRepository.existsByName(request.name())) {
        throw new DuplicateResourceException("Product", "name", request.name());
      }
      product.changeName(request.name());
    }
    if (request.description() != null) {
      product.changeDescription(request.description());
    }
    if (request.unitPrice() != null) {
      product.updatePrice(request.unitPrice());
    }
    if (request.categoryId() != null) {
      CategoryEntity category = categoryRepository.findById(request.categoryId())
          .orElseThrow(() -> new ResourceNotFoundException("Category", request.categoryId()));
      product.changeCategory(category);
    }
    ProductEntity updatedProduct = productRepository.save(product);
    return productMapper.toDto(updatedProduct);
  }

  @Transactional
  @Override
  public void deleteProduct(Long productId) {
    if (!productRepository.existsById(productId)) {
      throw new ResourceNotFoundException("Product", productId);
    }
    if (cartItemRepository.existsByProductIdAndActiveCart(productId)) {
      throw new InvalidOperationException("Cannot delete product: it is in one or more active carts");
    }
    if (orderItemRepository.existsByProductId(productId)) {
      throw new InvalidOperationException("Cannot delete product: it is in one or more orders");
    }
    productRepository.deleteById(productId);
  }

  @Transactional
  @Override
  public void restockProduct(Long productId, int quantity) {
    ProductEntity product = productRepository.findById(productId)
        .orElseThrow(() -> new ResourceNotFoundException("Product", productId));
    product.restock(quantity);
    productRepository.save(product);
  }
}
