package com.lautarorisso.eCommerce_api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lautarorisso.eCommerce_api.dto.request.CreateCategoryRequest;
import com.lautarorisso.eCommerce_api.dto.response.CategoryDto;
import com.lautarorisso.eCommerce_api.exceptions.ResourceNotFoundException;
import com.lautarorisso.eCommerce_api.mapper.CategoryMapper;
import com.lautarorisso.eCommerce_api.model.CategoryEntity;
import com.lautarorisso.eCommerce_api.repository.CategoryRepository;
import com.lautarorisso.eCommerce_api.repository.ProductRepository;
import com.lautarorisso.eCommerce_api.service.impl.CategoryServiceImpl;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

  @Mock
  private CategoryRepository categoryRepository;

  @Mock
  private CategoryMapper categoryMapper;

  @Mock
  private ProductRepository productRepository;

  @InjectMocks
  private CategoryServiceImpl categoryService;

  private final CategoryEntity electronics = new CategoryEntity("Electronics");
  private final CategoryDto electronicsDto = new CategoryDto(1L, "Electronics");

  @Test
  @DisplayName("createCategory - should create and return CategoryDto")
  void createCategory_withValidData_returnsDto() {
    var request = new CreateCategoryRequest("Electronics");

    when(categoryRepository.existsByName("Electronics")).thenReturn(false);
    when(categoryMapper.toEntity(request)).thenReturn(electronics);
    when(categoryRepository.save(electronics)).thenReturn(electronics);
    when(categoryMapper.toDto(electronics)).thenReturn(electronicsDto);

    var result = categoryService.createCategory(request);

    assertEquals("Electronics", result.name());
    verify(categoryRepository).existsByName("Electronics");
    verify(categoryRepository).save(electronics);
  }

  @Test
  @DisplayName("deleteCategory - should delete when category exists and has no products")
  void deleteCategory_whenExistsAndNoProducts_deletesSuccessfully() {
    when(categoryRepository.existsById(1L)).thenReturn(true);
    when(productRepository.existsByCategoryId(1L)).thenReturn(false);

    categoryService.deleteCategory(1L);

    verify(categoryRepository).deleteById(1L);
  }

  @Test
  @DisplayName("deleteCategory - should throw InvalidOperationException when category has products")
  void deleteCategory_whenHasProducts_throwsException() {
    when(categoryRepository.existsById(1L)).thenReturn(true);
    when(productRepository.existsByCategoryId(1L)).thenReturn(true);

    assertThrows(com.lautarorisso.eCommerce_api.exceptions.InvalidOperationException.class,
        () -> categoryService.deleteCategory(1L));

    verify(categoryRepository, never()).deleteById(any());
  }
}
