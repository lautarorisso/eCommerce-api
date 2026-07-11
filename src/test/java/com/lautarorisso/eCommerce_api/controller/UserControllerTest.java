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
import com.lautarorisso.eCommerce_api.dto.response.UserDto;
import com.lautarorisso.eCommerce_api.enums.Role;
import com.lautarorisso.eCommerce_api.security.CustomUserDetailsService;
import com.lautarorisso.eCommerce_api.security.JwtService;
import com.lautarorisso.eCommerce_api.service.UserService;

@WebMvcTest(UserController.class)
@Import(TestSecurityConfig.class)
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private UserService userService;

  @MockitoBean
  private JwtService jwtService;

  @MockitoBean
  private CustomUserDetailsService userDetailsService;

  private final UserDto userDto = new UserDto(1L, "johndoe", "user@example.com", Role.USER);

  @Test
  @DisplayName("GET /api/users/me - should return current user")
  void getCurrentUser_returnsUser() throws Exception {
    when(userService.getCurrentUser()).thenReturn(userDto);

    mockMvc.perform(get("/api/users/me"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("johndoe"));
  }

  @Test
  @DisplayName("POST /api/users - should create user and return 201")
  void createUser_withValidData_returns201() throws Exception {
    when(userService.createUser(any())).thenReturn(userDto);

    mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                { "username": "johndoe", "email": "user@example.com",
                  "password": "securePassword123", "role": "USER" }
                """))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.username").value("johndoe"));
  }

  @Test
  @DisplayName("GET /api/users/{id} - should return user by id")
  void getUserById_whenFound_returnsUser() throws Exception {
    when(userService.getUserById(1L)).thenReturn(userDto);

    mockMvc.perform(get("/api/users/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("johndoe"));
  }

  @Test
  @DisplayName("PATCH /api/users/{id} - should update user")
  void updateUser_withValidData_returnsUpdated() throws Exception {
    var updated = new UserDto(1L, "janedoe", "user@example.com", Role.USER);
    when(userService.updateUser(eq(1L), any())).thenReturn(updated);

    mockMvc.perform(patch("/api/users/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                { "username": "janedoe" }
                """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("janedoe"));
  }

  @Test
  @DisplayName("DELETE /api/users/{id} - should delete user and return 204")
  void deleteUser_whenValid_returns204() throws Exception {
    mockMvc.perform(delete("/api/users/1"))
        .andExpect(status().isNoContent());
  }
}
