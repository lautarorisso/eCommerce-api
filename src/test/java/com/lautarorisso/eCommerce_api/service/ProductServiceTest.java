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

import com.lautarorisso.eCommerce_api.dto.request.CreateProductRequest;
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
  @DisplayName("deleteProduct - should throw InvalidOperationException when product is in any cart")
  void deleteProduct_whenInCart_throwsException() {
    var productRef = new ProductEntity("Keyboard", "Mechanical keyboard", BigDecimal.valueOf(49.99), 10);
    when(productRepository.findById(1L)).thenReturn(Optional.of(productRef));
    when(cartItemRepository.existsByProductId(1L)).thenReturn(true);

    assertThrows(InvalidOperationException.class, () -> productService.deleteProduct(1L));
    verify(productRepository, never()).save(any());
  }

  @Test
  @DisplayName("deleteProduct - should soft delete product when no references")
  void deleteProduct_whenNoReferences_softDeletesProduct() {
    var productToDelete = new ProductEntity("Keyboard", "Mechanical keyboard", BigDecimal.valueOf(49.99), 10);
    when(productRepository.findById(1L)).thenReturn(Optional.of(productToDelete));
    when(cartItemRepository.existsByProductId(1L)).thenReturn(false);
    when(orderItemRepository.existsByProductId(1L)).thenReturn(false);
    when(productRepository.save(any())).thenReturn(productToDelete);

    productService.deleteProduct(1L);

    verify(productRepository).save(any());
    assertTrue(productToDelete.isDeleted());
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
  @DisplayName("getProductById - should return product when found")
  void getProductById_whenFound_returnsDto() {
    when(productRepository.findById(1L)).thenReturn(Optional.of(product));
    when(productMapper.toDto(product)).thenReturn(productDto);

    var result = productService.getProductById(1L);

    assertEquals("Mouse", result.name());
    verify(productRepository).findById(1L);
  }

  @Test
  @DisplayName("restoreProduct - should restore a soft-deleted product")
  void restoreProduct_whenDeleted_restoresAndReturnsDto() {
    var deleted = new ProductEntity("Keyboard", "Mechanical keyboard", BigDecimal.valueOf(49.99), 10);
    deleted.softDelete();
    when(productRepository.findDeletedById(1L)).thenReturn(Optional.of(deleted));
    when(productRepository.existsByName("Keyboard")).thenReturn(false);
    when(productRepository.save(any())).thenReturn(deleted);
    when(productMapper.toDto(deleted)).thenReturn(productDto);

    var result = productService.restoreProduct(1L);

    assertFalse(deleted.isDeleted());
    assertSame(productDto, result);
    verify(productRepository).save(deleted);
  }

  @Test
  @DisplayName("restoreProduct - should throw when no soft-deleted product exists")
  void restoreProduct_whenNotFound_throws() {
    when(productRepository.findDeletedById(1L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> productService.restoreProduct(1L));
    verify(productRepository, never()).save(any());
  }

  @Test
  @DisplayName("restoreProduct - should throw when an active product already has the name")
  void restoreProduct_whenNameTaken_throws() {
    var deleted = new ProductEntity("Mouse", "Wireless mouse", BigDecimal.valueOf(29.99), 100);
    deleted.softDelete();
    when(productRepository.findDeletedById(1L)).thenReturn(Optional.of(deleted));
    when(productRepository.existsByName("Mouse")).thenReturn(true);

    assertThrows(DuplicateResourceException.class, () -> productService.restoreProduct(1L));
    verify(productRepository, never()).save(any());
  }
}
