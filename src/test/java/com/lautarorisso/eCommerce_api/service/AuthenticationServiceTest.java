package com.lautarorisso.eCommerce_api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import com.lautarorisso.eCommerce_api.dto.request.CreateUserRequest;
import com.lautarorisso.eCommerce_api.dto.request.LoginRequest;
import com.lautarorisso.eCommerce_api.dto.response.AuthResponse;
import com.lautarorisso.eCommerce_api.dto.response.UserDto;
import com.lautarorisso.eCommerce_api.enums.Role;
import com.lautarorisso.eCommerce_api.exceptions.DuplicateResourceException;
import com.lautarorisso.eCommerce_api.model.UserEntity;
import com.lautarorisso.eCommerce_api.repository.UserRepository;
import com.lautarorisso.eCommerce_api.security.JwtService;
import com.lautarorisso.eCommerce_api.service.impl.AuthenticationServiceImpl;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

  @Mock
  private AuthenticationManager authenticationManager;

  @Mock
  private JwtService jwtService;

  @Mock
  private UserRepository userRepository;

  @Mock
  private UserService userService;

  @InjectMocks
  private AuthenticationServiceImpl authService;

  @Test
  @DisplayName("login - should return AuthResponse when credentials are valid")
  void login_withValidCredentials_returnsAuthResponse() {
    var loginRequest = new LoginRequest("user@example.com", "password123");
    var authToken = new UsernamePasswordAuthenticationToken("user@example.com", "password123");
    var user = new UserEntity("johndoe", "user@example.com", "encoded", Role.USER);

    when(authenticationManager.authenticate(any())).thenReturn(authToken);
    when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
    when(jwtService.createToken(user.getId(), user.getEmail(), user.getRole().name())).thenReturn("jwt-token");

    AuthResponse response = authService.login(loginRequest);

    assertNotNull(response);
    assertEquals("jwt-token", response.token());
    verify(authenticationManager).authenticate(any());
    verify(userRepository).findByEmail("user@example.com");
  }

  @Test
  @DisplayName("register - should create user and return AuthResponse")
  void register_withValidData_returnsAuthResponse() {
    var request = new CreateUserRequest("johndoe", "user@example.com", "password123", Role.USER);
    var user = new UserEntity("johndoe", "user@example.com", "encoded", Role.USER);

    when(userService.createUser(any())).thenReturn(new UserDto(1L, "johndoe", "user@example.com", Role.USER));
    when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
    when(jwtService.createToken(user.getId(), user.getEmail(), user.getRole().name())).thenReturn("jwt-token");

    AuthResponse response = authService.register(request);

    assertNotNull(response);
    assertEquals("jwt-token", response.token());
    verify(userService).createUser(any());
    verify(userRepository).findByEmail("user@example.com");
  }

  @Test
  @DisplayName("register - should throw DuplicateResourceException when email already exists")
  void register_withDuplicateEmail_throwsException() {
    var request = new CreateUserRequest("johndoe", "existing@example.com", "password123", Role.USER);

    when(userService.createUser(any())).thenThrow(new DuplicateResourceException("User", "email", "existing@example.com"));

    assertThrows(DuplicateResourceException.class, () -> authService.register(request));
    verify(userService).createUser(any());
  }
}
