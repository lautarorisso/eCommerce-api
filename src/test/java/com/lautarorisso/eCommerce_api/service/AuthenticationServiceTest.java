package com.lautarorisso.eCommerce_api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.lautarorisso.eCommerce_api.dto.request.LoginRequest;
import com.lautarorisso.eCommerce_api.dto.request.RegisterRequest;
import com.lautarorisso.eCommerce_api.dto.response.AuthResponse;
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
  private PasswordEncoder passwordEncoder;

  @InjectMocks
  private AuthenticationServiceImpl authService;

  @Test
  @DisplayName("login - should return AuthResponse when credentials are valid")
  void login_withValidCredentials_returnsAuthResponse() {
    var loginRequest = new LoginRequest("user@example.com", "password123");
    var authToken = new UsernamePasswordAuthenticationToken("user@example.com", "password123");
    var user = new UserEntity("johndoe", "user@example.com", "encoded", com.lautarorisso.eCommerce_api.enums.Role.USER);

    when(authenticationManager.authenticate(any())).thenReturn(authToken);
    when(userRepository.findByEmail("user@example.com")).thenReturn(java.util.Optional.of(user));
    when(jwtService.createToken(user.getId(), user.getEmail(), user.getRole().name())).thenReturn("jwt-token");

    AuthResponse response = authService.login(loginRequest);

    assertNotNull(response);
    assertEquals("jwt-token", response.token());
    verify(authenticationManager).authenticate(any());
    verify(userRepository).findByEmail("user@example.com");
  }

  @Test
  @DisplayName("login - should throw BadCredentialsException when credentials are invalid")
  void login_withInvalidCredentials_throwsException() {
    var loginRequest = new LoginRequest("user@example.com", "wrong");

    when(authenticationManager.authenticate(any()))
        .thenThrow(new BadCredentialsException("Bad credentials"));

    assertThrows(BadCredentialsException.class, () -> authService.login(loginRequest));
  }

  @Test
  @DisplayName("register - should create user and return AuthResponse")
  void register_withValidData_returnsAuthResponse() {
    var request = new RegisterRequest("johndoe", "user@example.com", "password123");
    var user = new UserEntity("johndoe", "user@example.com", "encoded", com.lautarorisso.eCommerce_api.enums.Role.USER);

    when(userRepository.existsByEmail("user@example.com")).thenReturn(false);
    when(passwordEncoder.encode("password123")).thenReturn("encoded");
    when(userRepository.save(any())).thenReturn(user);
    when(jwtService.createToken(user.getId(), user.getEmail(), user.getRole().name())).thenReturn("jwt-token");

    AuthResponse response = authService.register(request);

    assertNotNull(response);
    assertEquals("jwt-token", response.token());
    verify(userRepository).existsByEmail("user@example.com");
    verify(passwordEncoder).encode("password123");
    verify(userRepository).save(any());
  }

  @Test
  @DisplayName("register - should throw DuplicateResourceException when email already exists")
  void register_withDuplicateEmail_throwsException() {
    var request = new RegisterRequest("johndoe", "existing@example.com", "password123");

    when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

    assertThrows(DuplicateResourceException.class, () -> authService.register(request));
    verify(userRepository).existsByEmail("existing@example.com");
    verify(userRepository, never()).save(any());
  }
}
