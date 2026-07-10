package com.lautarorisso.eCommerce_api.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.lautarorisso.eCommerce_api.config.TestSecurityConfig;
import com.lautarorisso.eCommerce_api.dto.response.CategoryDto;
import com.lautarorisso.eCommerce_api.exceptions.ResourceNotFoundException;
import com.lautarorisso.eCommerce_api.security.CustomUserDetailsService;
import com.lautarorisso.eCommerce_api.security.JwtService;
import com.lautarorisso.eCommerce_api.service.CategoryService;

@WebMvcTest(CategoryController.class)
@Import(TestSecurityConfig.class)
class CategoryControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private CategoryService categoryService;

  @MockitoBean
  private JwtService jwtService;

  @MockitoBean
  private CustomUserDetailsService userDetailsService;

  private final CategoryDto electronics = new CategoryDto(1L, "Electronics");

  @Test
  @DisplayName("GET /api/categories - should return all categories")
  void getAllCategories_returns200() throws Exception {
    when(categoryService.getAllCategories()).thenReturn(List.of(electronics));

    mockMvc.perform(get("/api/categories"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].name").value("Electronics"));
  }

  @Test
  @DisplayName("GET /api/categories/{id} - should return category")
  void getCategoryById_whenExists_returns200() throws Exception {
    when(categoryService.getCategoryById(1L)).thenReturn(electronics);

    mockMvc.perform(get("/api/categories/{id}", 1L))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Electronics"));
  }

  @Test
  @DisplayName("GET /api/categories/{id} - should return 404 when not found")
  void getCategoryById_whenNotExists_returns404() throws Exception {
    when(categoryService.getCategoryById(99L)).thenThrow(new ResourceNotFoundException("Category", 99L));

    mockMvc.perform(get("/api/categories/{id}", 99L))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("POST /api/categories - should create and return 201")
  void createCategory_returns201() throws Exception {
    when(categoryService.createCategory(any())).thenReturn(electronics);

    mockMvc.perform(post("/api/categories")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                    { "name": "Electronics" }
                    """))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("Electronics"));
  }

  @Test
  @DisplayName("DELETE /api/categories/{id} - should return 204")
  void deleteCategory_returns204() throws Exception {
    doNothing().when(categoryService).deleteCategory(1L);

    mockMvc.perform(delete("/api/categories/{id}", 1L))
        .andExpect(status().isNoContent());
  }
}
