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
import com.lautarorisso.eCommerce_api.exceptions.InvalidOperationException;
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
}
