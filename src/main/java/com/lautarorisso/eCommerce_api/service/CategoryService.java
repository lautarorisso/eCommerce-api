package com.lautarorisso.eCommerce_api.service;

import java.util.List;

import com.lautarorisso.eCommerce_api.dto.request.CreateCategoryRequest;
import com.lautarorisso.eCommerce_api.dto.request.UpdateCategoryRequest;
import com.lautarorisso.eCommerce_api.dto.response.CategoryDto;

public interface CategoryService {
  CategoryDto createCategory(CreateCategoryRequest request);

  List<CategoryDto> getAllCategories();

  CategoryDto getCategoryById(Long id);

  CategoryDto updateCategory(Long id, UpdateCategoryRequest request);

  void deleteCategory(Long id);
}
