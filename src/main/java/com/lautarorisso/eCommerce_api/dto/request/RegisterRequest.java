package com.lautarorisso.eCommerce_api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Registration information")
public record RegisterRequest(
    @Schema(description = "Desired username", example = "johndoe")
    @NotBlank String username,

    @Schema(description = "User email", example = "user@example.com")
    @NotBlank @Email String email,

    @Schema(description = "Password", example = "securePassword123")
    @NotBlank String password) {
}
