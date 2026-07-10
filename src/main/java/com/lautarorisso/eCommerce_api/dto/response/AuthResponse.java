package com.lautarorisso.eCommerce_api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Authentication response with JWT token")
public record AuthResponse(
    @Schema(description = "JWT token for authenticated requests", example = "eyJhbGciOiJIUzI1NiIs...")
    String token,

    @Schema(description = "User ID", example = "1")
    Long userId,

    @Schema(description = "User email", example = "user@example.com")
    String email,

    @Schema(description = "User role", example = "ROLE_USER")
    String role) {
}
