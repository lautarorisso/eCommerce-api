package com.lautarorisso.eCommerce_api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lautarorisso.eCommerce_api.dto.request.CreateCategoryRequest;
import com.lautarorisso.eCommerce_api.dto.request.UpdateCategoryRequest;
import com.lautarorisso.eCommerce_api.dto.response.CategoryDto;
import com.lautarorisso.eCommerce_api.exceptions.DuplicateResourceException;
import com.lautarorisso.eCommerce_api.exceptions.ResourceNotFoundException;
import com.lautarorisso.eCommerce_api.mapper.CategoryMapper;
import com.lautarorisso.eCommerce_api.model.CategoryEntity;
import com.lautarorisso.eCommerce_api.repository.CategoryRepository;
import com.lautarorisso.eCommerce_api.service.impl.CategoryServiceImpl;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

  @Mock
  private CategoryRepository categoryRepository;

  @Mock
  private CategoryMapper categoryMapper;

  @InjectMocks
  private CategoryServiceImpl categoryService;

  private final CategoryEntity electronics = new CategoryEntity("Electronics");
  private final CategoryDto electronicsDto = new CategoryDto(1L, "Electronics");
  private final CategoryEntity books = new CategoryEntity("Books");
  private final CategoryDto booksDto = new CategoryDto(2L, "Books");

  @Test
  @DisplayName("getAllCategories - should return all categories")
  void getAllCategories_returnsList() {
    when(categoryRepository.findAll()).thenReturn(List.of(electronics, books));
    when(categoryMapper.toDto(electronics)).thenReturn(electronicsDto);
    when(categoryMapper.toDto(books)).thenReturn(booksDto);

    var result = categoryService.getAllCategories();

    assertEquals(2, result.size());
    verify(categoryRepository).findAll();
  }

  @Test
  @DisplayName("getCategoryById - should return CategoryDto when category exists")
  void getCategoryById_whenExists_returnsDto() {
    when(categoryRepository.findById(1L)).thenReturn(Optional.of(electronics));
    when(categoryMapper.toDto(electronics)).thenReturn(electronicsDto);

    var result = categoryService.getCategoryById(1L);

    assertEquals("Electronics", result.name());
    verify(categoryRepository).findById(1L);
  }

  @Test
  @DisplayName("getCategoryById - should throw ResourceNotFoundException when category does not exist")
  void getCategoryById_whenNotExists_throwsException() {
    when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> categoryService.getCategoryById(99L));
  }

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
  @DisplayName("createCategory - should throw DuplicateResourceException when name already exists")
  void createCategory_withDuplicateName_throwsException() {
    var request = new CreateCategoryRequest("Electronics");

    when(categoryRepository.existsByName("Electronics")).thenReturn(true);

    assertThrows(DuplicateResourceException.class, () -> categoryService.createCategory(request));
    verify(categoryRepository, never()).save(any());
  }

  @Test
  @DisplayName("updateCategory - should update and return CategoryDto")
  void updateCategory_withValidData_returnsDto() {
    var request = new UpdateCategoryRequest("Home Electronics");

    when(categoryRepository.findById(1L)).thenReturn(Optional.of(electronics));
    when(categoryRepository.existsByName("Home Electronics")).thenReturn(false);
    when(categoryRepository.save(electronics)).thenReturn(electronics);
    when(categoryMapper.toDto(electronics)).thenReturn(new CategoryDto(1L, "Home Electronics"));

    var result = categoryService.updateCategory(1L, request);

    assertEquals("Home Electronics", result.name());
    verify(categoryRepository).findById(1L);
    verify(categoryRepository).save(electronics);
  }

  @Test
  @DisplayName("deleteCategory - should delete when category exists")
  void deleteCategory_whenExists_deletesSuccessfully() {
    when(categoryRepository.existsById(1L)).thenReturn(true);

    categoryService.deleteCategory(1L);

    verify(categoryRepository).deleteById(1L);
  }

  @Test
  @DisplayName("deleteCategory - should throw ResourceNotFoundException when category does not exist")
  void deleteCategory_whenNotExists_throwsException() {
    when(categoryRepository.existsById(99L)).thenReturn(false);

    assertThrows(ResourceNotFoundException.class, () -> categoryService.deleteCategory(99L));
    verify(categoryRepository, never()).deleteById(any());
  }
}
