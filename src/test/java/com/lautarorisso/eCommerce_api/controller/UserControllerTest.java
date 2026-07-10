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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.lautarorisso.eCommerce_api.config.TestSecurityConfig;
import com.lautarorisso.eCommerce_api.dto.response.UserDto;
import com.lautarorisso.eCommerce_api.enums.Role;
import com.lautarorisso.eCommerce_api.exceptions.ResourceNotFoundException;
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

  private final UserDto user = new UserDto(1L, "johndoe", "user@example.com", Role.USER);

  @Test
  @DisplayName("GET /api/users - should return paginated users")
  void getAllUsers_returns200() throws Exception {
    var page = new PageImpl<>(java.util.List.of(user), PageRequest.of(0, 20), 1);
    when(userService.getAllUsers(any(), any(), any())).thenReturn(page);

    mockMvc.perform(get("/api/users"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].username").value("johndoe"));
  }

  @Test
  @DisplayName("GET /api/users/{userId} - should return user")
  void getUserById_whenExists_returns200() throws Exception {
    when(userService.getUserById(1L)).thenReturn(user);

    mockMvc.perform(get("/api/users/{userId}", 1L))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value("user@example.com"));
  }

  @Test
  @DisplayName("GET /api/users/{userId} - should return 404 when not found")
  void getUserById_whenNotExists_returns404() throws Exception {
    when(userService.getUserById(99L)).thenThrow(new ResourceNotFoundException("User", 99L));

    mockMvc.perform(get("/api/users/{userId}", 99L))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("POST /api/users - should create and return 201")
  void createUser_returns201() throws Exception {
    when(userService.createUser(any())).thenReturn(user);

    mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                    { "username": "johndoe", "email": "user@example.com", "password": "pass123" }
                    """))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.username").value("johndoe"));
  }

  @Test
  @DisplayName("DELETE /api/users/{userId} - should return 204")
  void deleteUser_returns204() throws Exception {
    doNothing().when(userService).deleteUser(1L);

    mockMvc.perform(delete("/api/users/{userId}", 1L))
        .andExpect(status().isNoContent());
  }
}
