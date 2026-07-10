package com.lautarorisso.eCommerce_api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;

@Schema(description = "Request to update an existing user (admin only)")
public record UpdateUserRequest(
    @Schema(description = "Username", example = "johndoe")
    String username,

    @Schema(description = "User email", example = "user@example.com")
    @Email String email) {
}
