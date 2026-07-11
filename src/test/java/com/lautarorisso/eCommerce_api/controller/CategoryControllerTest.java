package com.lautarorisso.eCommerce_api.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

  private final CategoryDto categoryDto = new CategoryDto(1L, "Electronics");

  @Test
  @DisplayName("GET /api/categories - should return all categories")
  void getAllCategories_returnsList() throws Exception {
    when(categoryService.getAllCategories()).thenReturn(java.util.List.of(categoryDto));

    mockMvc.perform(get("/api/categories"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].name").value("Electronics"));
  }

  @Test
  @DisplayName("GET /api/categories/{id} - should return category by id")
  void getCategoryById_whenFound_returnsCategory() throws Exception {
    when(categoryService.getCategoryById(1L)).thenReturn(categoryDto);

    mockMvc.perform(get("/api/categories/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Electronics"));
  }

  @Test
  @DisplayName("POST /api/categories - should create category and return 201")
  void createCategory_withValidData_returns201() throws Exception {
    when(categoryService.createCategory(any())).thenReturn(categoryDto);

    mockMvc.perform(post("/api/categories")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                { "name": "Electronics" }
                """))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("Electronics"));
  }

  @Test
  @DisplayName("PATCH /api/categories/{id} - should update category")
  void updateCategory_withValidData_returnsUpdated() throws Exception {
    var updated = new CategoryDto(1L, "Home Electronics");
    when(categoryService.updateCategory(eq(1L), any())).thenReturn(updated);

    mockMvc.perform(patch("/api/categories/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                { "name": "Home Electronics" }
                """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Home Electronics"));
  }

  @Test
  @DisplayName("DELETE /api/categories/{id} - should delete category and return 204")
  void deleteCategory_whenValid_returns204() throws Exception {
    mockMvc.perform(delete("/api/categories/1"))
        .andExpect(status().isNoContent());
  }
}
