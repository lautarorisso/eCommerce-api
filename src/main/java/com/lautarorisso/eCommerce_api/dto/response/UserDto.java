package com.lautarorisso.eCommerce_api.dto.response;

import com.lautarorisso.eCommerce_api.enums.Role;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "User information")
public record UserDto(
    @Schema(description = "User ID", example = "1")
    Long id,

    @Schema(description = "Username", example = "johndoe")
    String username,

    @Schema(description = "User email", example = "user@example.com")
    String email,

    @Schema(description = "User role", example = "USER")
    Role role) {
}
