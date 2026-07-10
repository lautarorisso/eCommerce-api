package com.lautarorisso.eCommerce_api.controller;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.lautarorisso.eCommerce_api.dto.request.CreateProductRequest;
import com.lautarorisso.eCommerce_api.dto.request.UpdateProductRequest;
import com.lautarorisso.eCommerce_api.dto.response.ProductDto;
import com.lautarorisso.eCommerce_api.service.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Manage products")
public class ProductController {

  private final ProductService productService;

  @GetMapping
  @Operation(summary = "Get all products", description = "Returns a paginated list of products with optional filters")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Products found")
  })
  public Page<ProductDto> getAllProducts(
      @RequestParam(required = false) String search,
      @RequestParam(required = false) BigDecimal minPrice,
      @RequestParam(required = false) BigDecimal maxPrice,
      @RequestParam(required = false) Boolean inStock,
      @RequestParam(required = false) Long categoryId,
      @PageableDefault(size = 20) Pageable pageable) {
    return productService.getAllProducts(search, minPrice, maxPrice, inStock, categoryId, pageable);
  }

  @GetMapping("/{productId}")
  @Operation(summary = "Get product by ID", description = "Returns a single product")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Product found"),
      @ApiResponse(responseCode = "404", description = "Product not found")
  })
  public ProductDto getProductById(@PathVariable Long productId) {
    return productService.getProductById(productId);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Create a product", description = "Creates a new product (admin only)")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Product created"),
      @ApiResponse(responseCode = "400", description = "Invalid input"),
      @ApiResponse(responseCode = "401", description = "Unauthorized"),
      @ApiResponse(responseCode = "409", description = "Product with same name already exists")
  })
  public ProductDto createProduct(@Valid @RequestBody CreateProductRequest request) {
    return productService.createProduct(request);
  }

  @PatchMapping("/{productId}")
  @Operation(summary = "Update a product", description = "Updates some fields of a product (admin only)")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Product updated"),
      @ApiResponse(responseCode = "400", description = "Invalid input"),
      @ApiResponse(responseCode = "401", description = "Unauthorized"),
      @ApiResponse(responseCode = "404", description = "Product not found"),
      @ApiResponse(responseCode = "409", description = "Product with same name already exists")
  })
  public ProductDto updateProduct(@PathVariable Long productId, @Valid @RequestBody UpdateProductRequest request) {
    return productService.updateProduct(productId, request);
  }

  @DeleteMapping("/{productId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Delete a product", description = "Deletes a product by ID (admin only)")
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "Product deleted"),
      @ApiResponse(responseCode = "401", description = "Unauthorized"),
      @ApiResponse(responseCode = "404", description = "Product not found")
  })
  public void deleteProduct(@PathVariable Long productId) {
    productService.deleteProduct(productId);
  }

  @PatchMapping("/{productId}/restock")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Restock a product", description = "Adds stock quantity to a product (admin only)")
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "Stock updated"),
      @ApiResponse(responseCode = "400", description = "Invalid quantity"),
      @ApiResponse(responseCode = "401", description = "Unauthorized"),
      @ApiResponse(responseCode = "404", description = "Product not found")
  })
  public void restockProduct(@PathVariable Long productId, @RequestParam int quantity) {
    productService.restockProduct(productId, quantity);
  }
}
