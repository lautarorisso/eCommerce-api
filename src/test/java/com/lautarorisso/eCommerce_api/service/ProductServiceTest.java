package com.lautarorisso.eCommerce_api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

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
import com.lautarorisso.eCommerce_api.service.impl.ProductServiceImpl;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

  @Mock
  private ProductMapper productMapper;

  @Mock
  private ProductRepository productRepository;

  @Mock
  private CartItemRepository cartItemRepository;

  @Mock
  private OrderItemRepository orderItemRepository;

  @Mock
  private CategoryRepository categoryRepository;

  @InjectMocks
  private ProductServiceImpl productService;

  @Captor
  private ArgumentCaptor<ProductEntity> productCaptor;

  private final ProductEntity product = new ProductEntity("Mouse", "Wireless mouse", BigDecimal.valueOf(29.99), 100);
  private final ProductDto productDto = new ProductDto(1L, "Mouse", "Wireless mouse", BigDecimal.valueOf(29.99), 100, 1L,
      "Electronics");

  @Test
  @DisplayName("getAllProducts - should return paginated products with filters")
  void getAllProducts_withFilters_returnsPage() {
    var pageable = PageRequest.of(0, 20);
    var page = new PageImpl<>(java.util.List.of(product));

    when(productRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
    when(productMapper.toDto(product)).thenReturn(productDto);

    Page<ProductDto> result = productService.getAllProducts("mouse", BigDecimal.TEN, BigDecimal.valueOf(100), true, 1L,
        pageable);

    assertEquals(1, result.getTotalElements());
    assertEquals("Mouse", result.getContent().get(0).name());
    verify(productRepository).findAll(any(Specification.class), eq(pageable));
  }

  @Test
  @DisplayName("getProductById - should return ProductDto when product exists")
  void getProductById_whenExists_returnsDto() {
    when(productRepository.findById(1L)).thenReturn(Optional.of(product));
    when(productMapper.toDto(product)).thenReturn(productDto);

    ProductDto result = productService.getProductById(1L);

    assertEquals("Mouse", result.name());
    verify(productRepository).findById(1L);
  }

  @Test
  @DisplayName("getProductById - should throw ResourceNotFoundException when product does not exist")
  void getProductById_whenNotExists_throwsException() {
    when(productRepository.findById(99L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(99L));
  }

  @Test
  @DisplayName("createProduct - should create and return ProductDto")
  void createProduct_withValidData_returnsDto() {
    var request = new CreateProductRequest("Mouse", "Wireless mouse", BigDecimal.valueOf(29.99), 100, 1L);
    var category = new CategoryEntity("Electronics");

    when(productRepository.existsByName("Mouse")).thenReturn(false);
    when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
    when(productMapper.toEntity(request)).thenReturn(product);
    when(productRepository.save(any())).thenReturn(product);
    when(productMapper.toDto(product)).thenReturn(productDto);

    ProductDto result = productService.createProduct(request);

    assertEquals("Mouse", result.name());
    verify(productRepository).existsByName("Mouse");
    verify(categoryRepository).findById(1L);
    verify(productRepository).save(productCaptor.capture());
    assertSame(category, productCaptor.getValue().getCategory());
  }

  @Test
  @DisplayName("createProduct - should throw DuplicateResourceException when name already exists")
  void createProduct_withDuplicateName_throwsException() {
    var request = new CreateProductRequest("Mouse", "Wireless mouse", BigDecimal.valueOf(29.99), 100, 1L);

    when(productRepository.existsByName("Mouse")).thenReturn(true);

    assertThrows(DuplicateResourceException.class, () -> productService.createProduct(request));
    verify(productRepository, never()).save(any());
  }

  @Test
  @DisplayName("updateProduct - should update and return ProductDto")
  void updateProduct_withValidData_returnsDto() {
    var request = new UpdateProductRequest("Gaming Mouse", "Gaming mouse", BigDecimal.valueOf(49.99), 1L);
    var category = new CategoryEntity("Electronics");

    when(productRepository.findById(1L)).thenReturn(Optional.of(product));
    when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
    when(productRepository.save(product)).thenReturn(product);
    when(productMapper.toDto(product)).thenReturn(productDto);

    ProductDto result = productService.updateProduct(1L, request);

    assertNotNull(result);
    verify(productRepository).findById(1L);
    verify(productRepository).save(product);
  }

  @Test
  @DisplayName("deleteProduct - should delete when product exists and is not referenced")
  void deleteProduct_whenValid_deletesSuccessfully() {
    when(productRepository.existsById(1L)).thenReturn(true);
    when(cartItemRepository.existsByProductId(1L)).thenReturn(false);
    when(orderItemRepository.existsByProductId(1L)).thenReturn(false);

    productService.deleteProduct(1L);

    verify(productRepository).deleteById(1L);
  }

  @Test
  @DisplayName("deleteProduct - should throw InvalidOperationException when product is in a cart")
  void deleteProduct_whenInCart_throwsException() {
    when(productRepository.existsById(1L)).thenReturn(true);
    when(cartItemRepository.existsByProductId(1L)).thenReturn(true);

    assertThrows(InvalidOperationException.class, () -> productService.deleteProduct(1L));
    verify(productRepository, never()).deleteById(any());
  }

  @Test
  @DisplayName("restockProduct - should increase stock")
  void restockProduct_withValidQuantity_increasesStock() {
    when(productRepository.findById(1L)).thenReturn(Optional.of(product));

    productService.restockProduct(1L, 50);

    assertEquals(150, product.getStock());
    verify(productRepository).save(product);
  }

  @Test
  @DisplayName("restockProduct - should throw ResourceNotFoundException when product does not exist")
  void restockProduct_whenNotExists_throwsException() {
    when(productRepository.findById(99L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> productService.restockProduct(99L, 10));
  }
}
