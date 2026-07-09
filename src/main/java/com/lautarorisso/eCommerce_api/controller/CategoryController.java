package com.lautarorisso.eCommerce_api.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.lautarorisso.eCommerce_api.dto.request.CreateCategoryRequest;
import com.lautarorisso.eCommerce_api.dto.request.UpdateCategoryRequest;
import com.lautarorisso.eCommerce_api.dto.response.CategoryDto;
import com.lautarorisso.eCommerce_api.service.CategoryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

  private final CategoryService categoryService;

  @GetMapping
  public List<CategoryDto> getAllCategories() {
    return categoryService.getAllCategories();
  }

  @GetMapping("/{id}")
  public CategoryDto getCategoryById(@PathVariable Long id) {
    return categoryService.getCategoryById(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public CategoryDto createCategory(@Valid @RequestBody CreateCategoryRequest request) {
    return categoryService.createCategory(request);
  }

  @PutMapping("/{id}")
  public CategoryDto updateCategory(@PathVariable Long id, @Valid @RequestBody UpdateCategoryRequest request) {
    return categoryService.updateCategory(id, request);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteCategory(@PathVariable Long id) {
    categoryService.deleteCategory(id);
  }
}
