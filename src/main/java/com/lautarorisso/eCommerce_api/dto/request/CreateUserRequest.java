package com.lautarorisso.eCommerce_api.dto.request;

import com.lautarorisso.eCommerce_api.enums.Role;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request to create a new user (admin only)")
public record CreateUserRequest(
    @Schema(description = "Username", example = "johndoe")
    @NotBlank @Size(min = 3, max = 50) String username,

    @Schema(description = "User email", example = "user@example.com")
    @NotBlank @Email @Size(max = 100) String email,

    @Schema(description = "Password", example = "securePassword123")
    @NotBlank @Size(min = 6, max = 100) String password,

    @Schema(description = "User role", example = "USER")
    Role role) {
  public CreateUserRequest {
    if (role == null) role = Role.USER;
  }
}
