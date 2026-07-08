package com.lautarorisso.eCommerce_api.controller;

import java.util.List;

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

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

  private final ProductService productService;

  @GetMapping
  public List<ProductDto> getAllProducts() {
    return productService.getAllProducts();
  }

  @GetMapping("/{productId}")
  public ProductDto getProductById(@PathVariable Long productId) {
    return productService.getProductById(productId);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ProductDto createProduct(@Valid @RequestBody CreateProductRequest request) {
    return productService.createProduct(request);
  }

  @PutMapping("/{productId}")
  public ProductDto updateProduct(@PathVariable Long productId, @Valid @RequestBody UpdateProductRequest request) {
    return productService.updateProduct(productId, request);
  }

  @DeleteMapping("/{productId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteProduct(@PathVariable Long productId) {
    productService.deleteProduct(productId);
  }

  @PatchMapping("/{productId}/restock")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void restockProduct(@PathVariable Long productId, @RequestParam int quantity) {
    productService.restockProduct(productId, quantity);
  }
}
