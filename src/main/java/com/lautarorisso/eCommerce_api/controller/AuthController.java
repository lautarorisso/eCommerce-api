package com.lautarorisso.eCommerce_api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.lautarorisso.eCommerce_api.dto.request.LoginRequest;
import com.lautarorisso.eCommerce_api.dto.request.RegisterRequest;
import com.lautarorisso.eCommerce_api.dto.response.AuthResponse;
import com.lautarorisso.eCommerce_api.service.AuthenticationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User login and registration")
public class AuthController {

  private final AuthenticationService authService;

  @PostMapping("/login")
  @Operation(summary = "Login", description = "Authenticates a user and returns a JWT token")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Login successful, token returned"),
      @ApiResponse(responseCode = "401", description = "Invalid email or password")
  })
  public AuthResponse login(@Valid @RequestBody LoginRequest request) {
    return authService.login(request);
  }

  @PostMapping("/register")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Register", description = "Creates a new user account and returns a JWT token")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Account created successfully"),
      @ApiResponse(responseCode = "400", description = "Invalid input"),
      @ApiResponse(responseCode = "409", description = "Email already in use")
  })
  public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
    return authService.register(request);
  }
}
