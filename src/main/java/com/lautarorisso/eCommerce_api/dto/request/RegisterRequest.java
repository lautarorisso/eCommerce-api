package com.lautarorisso.eCommerce_api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Registration information")
public record RegisterRequest(
    @Schema(description = "Desired username", example = "johndoe")
    @NotBlank @Size(min = 3, max = 50) String username,

    @Schema(description = "User email", example = "user@example.com")
    @NotBlank @Email @Size(max = 100) String email,

    @Schema(description = "Password", example = "securePassword123")
    @NotBlank @Size(min = 6, max = 100) String password) {
}
