package com.lautarorisso.eCommerce_api.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lautarorisso.eCommerce_api.dto.request.CreateCategoryRequest;
import com.lautarorisso.eCommerce_api.dto.request.UpdateCategoryRequest;
import com.lautarorisso.eCommerce_api.dto.response.CategoryDto;
import com.lautarorisso.eCommerce_api.exceptions.DuplicateResourceException;
import com.lautarorisso.eCommerce_api.exceptions.InvalidOperationException;
import com.lautarorisso.eCommerce_api.exceptions.ResourceNotFoundException;
import com.lautarorisso.eCommerce_api.mapper.CategoryMapper;
import com.lautarorisso.eCommerce_api.model.CategoryEntity;
import com.lautarorisso.eCommerce_api.repository.CategoryRepository;
import com.lautarorisso.eCommerce_api.repository.ProductRepository;
import com.lautarorisso.eCommerce_api.service.CategoryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

  private final CategoryRepository categoryRepository;
  private final CategoryMapper categoryMapper;
  private final ProductRepository productRepository;

  @Transactional
  @Override
  public CategoryDto createCategory(CreateCategoryRequest request) {
    if (categoryRepository.existsByName(request.name())) {
      throw new DuplicateResourceException("Category", "name", request.name());
    }
    CategoryEntity entity = categoryMapper.toEntity(request);
    return categoryMapper.toDto(categoryRepository.save(entity));
  }

  @Transactional(readOnly = true)
  @Override
  public List<CategoryDto> getAllCategories() {
    return categoryRepository.findAll().stream()
        .map(categoryMapper::toDto)
        .toList();
  }

  @Transactional(readOnly = true)
  @Override
  public CategoryDto getCategoryById(Long id) {
    CategoryEntity entity = categoryRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Category", id));
    return categoryMapper.toDto(entity);
  }

  @Transactional
  @Override
  public CategoryDto updateCategory(Long id, UpdateCategoryRequest request) {
    CategoryEntity entity = categoryRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Category", id));
    if (!entity.getName().equals(request.name()) && categoryRepository.existsByName(request.name())) {
      throw new DuplicateResourceException("Category", "name", request.name());
    }
    entity.changeName(request.name());
    return categoryMapper.toDto(categoryRepository.save(entity));
  }

  @Transactional
  @Override
  public void deleteCategory(Long id) {
    CategoryEntity category = categoryRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Category", id));
    if (productRepository.existsByCategoryId(id)) {
      throw new InvalidOperationException("Cannot delete category: it has associated products");
    }
    categoryRepository.delete(category);
  }
}
