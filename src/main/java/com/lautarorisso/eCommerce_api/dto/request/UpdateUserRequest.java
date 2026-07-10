package com.lautarorisso.eCommerce_api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

@Schema(description = "Request to update an existing user (admin only)")
public record UpdateUserRequest(
    @Schema(description = "Username", example = "johndoe")
    @Size(min = 3, max = 50) String username,

    @Schema(description = "User email", example = "user@example.com")
    @Email @Size(max = 100) String email) {
}
