package com.lautarorisso.eCommerce_api.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.lautarorisso.eCommerce_api.config.TestSecurityConfig;
import com.lautarorisso.eCommerce_api.dto.request.CreateProductRequest;
import com.lautarorisso.eCommerce_api.dto.response.ProductDto;
import com.lautarorisso.eCommerce_api.exceptions.ResourceNotFoundException;
import com.lautarorisso.eCommerce_api.security.CustomUserDetailsService;
import com.lautarorisso.eCommerce_api.security.JwtService;
import com.lautarorisso.eCommerce_api.service.ProductService;

@WebMvcTest(ProductController.class)
@Import(TestSecurityConfig.class)
class ProductControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private ProductService productService;

  @MockitoBean
  private JwtService jwtService;

  @MockitoBean
  private CustomUserDetailsService userDetailsService;

  private final ProductDto mouse = new ProductDto(1L, "Mouse", "Wireless mouse", BigDecimal.valueOf(29.99), 100, 1L,
      "Electronics");

  @Test
  @DisplayName("GET /api/products - should return paginated products")
  void getAllProducts_returns200() throws Exception {
    var page = new PageImpl<>(java.util.List.of(mouse), PageRequest.of(0, 20), 1);
    when(productService.getAllProducts(any(), any(), any(), any(), any(), any())).thenReturn(page);

    mockMvc.perform(get("/api/products"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].name").value("Mouse"))
        .andExpect(jsonPath("$.totalElements").value(1));
  }

  @Test
  @DisplayName("GET /api/products/{id} - should return product")
  void getProductById_whenExists_returns200() throws Exception {
    when(productService.getProductById(1L)).thenReturn(mouse);

    mockMvc.perform(get("/api/products/{id}", 1L))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Mouse"))
        .andExpect(jsonPath("$.unitPrice").value(29.99));
  }

  @Test
  @DisplayName("GET /api/products/{id} - should return 404 when not found")
  void getProductById_whenNotExists_returns404() throws Exception {
    when(productService.getProductById(99L)).thenThrow(new ResourceNotFoundException("Product", 99L));

    mockMvc.perform(get("/api/products/{id}", 99L))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("POST /api/products - should create and return 201")
  void createProduct_withValidData_returns201() throws Exception {
    when(productService.createProduct(any())).thenReturn(mouse);

    mockMvc.perform(post("/api/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                    { "name": "Mouse", "description": "Wireless mouse",
                      "unitPrice": 29.99, "stock": 100, "categoryId": 1 }
                    """))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("Mouse"));
  }

  @Test
  @DisplayName("POST /api/products - should return 400 when name is missing")
  void createProduct_withMissingName_returns400() throws Exception {
    mockMvc.perform(post("/api/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                    { "description": "desc", "unitPrice": 10, "stock": 5, "categoryId": 1 }
                    """))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("DELETE /api/products/{id} - should return 204")
  void deleteProduct_returns204() throws Exception {
    doNothing().when(productService).deleteProduct(1L);

    mockMvc.perform(delete("/api/products/{id}", 1L))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("PATCH /api/products/{id}/restock - should return 204")
  void restockProduct_returns204() throws Exception {
    doNothing().when(productService).restockProduct(1L, 50);

    mockMvc.perform(patch("/api/products/{id}/restock", 1L)
            .param("quantity", "50"))
        .andExpect(status().isNoContent());
  }
}
