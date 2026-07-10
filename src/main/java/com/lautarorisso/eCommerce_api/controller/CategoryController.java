package com.lautarorisso.eCommerce_api.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.lautarorisso.eCommerce_api.dto.request.CreateCategoryRequest;
import com.lautarorisso.eCommerce_api.dto.request.UpdateCategoryRequest;
import com.lautarorisso.eCommerce_api.dto.response.CategoryDto;
import com.lautarorisso.eCommerce_api.service.CategoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "Manage product categories")
public class CategoryController {

  private final CategoryService categoryService;

  @GetMapping
  @Operation(summary = "Get all categories", description = "Returns a list of all categories")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Categories found")
  })
  public List<CategoryDto> getAllCategories() {
    return categoryService.getAllCategories();
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get category by ID", description = "Returns a single category")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Category found"),
      @ApiResponse(responseCode = "404", description = "Category not found")
  })
  public CategoryDto getCategoryById(@PathVariable Long id) {
    return categoryService.getCategoryById(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Create a category", description = "Creates a new category (admin only)")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Category created"),
      @ApiResponse(responseCode = "400", description = "Invalid input"),
      @ApiResponse(responseCode = "401", description = "Unauthorized"),
      @ApiResponse(responseCode = "409", description = "Category with same name already exists")
  })
  public CategoryDto createCategory(@Valid @RequestBody CreateCategoryRequest request) {
    return categoryService.createCategory(request);
  }

  @PatchMapping("/{id}")
  @Operation(summary = "Update a category", description = "Updates a category name by ID (admin only)")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Category updated"),
      @ApiResponse(responseCode = "400", description = "Invalid input"),
      @ApiResponse(responseCode = "401", description = "Unauthorized"),
      @ApiResponse(responseCode = "404", description = "Category not found"),
      @ApiResponse(responseCode = "409", description = "Category with same name already exists")
  })
  public CategoryDto updateCategory(@PathVariable Long id, @Valid @RequestBody UpdateCategoryRequest request) {
    return categoryService.updateCategory(id, request);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Delete a category", description = "Deletes a category by ID (admin only)")
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "Category deleted"),
      @ApiResponse(responseCode = "401", description = "Unauthorized"),
      @ApiResponse(responseCode = "404", description = "Category not found")
  })
  public void deleteCategory(@PathVariable Long id) {
    categoryService.deleteCategory(id);
  }
}
