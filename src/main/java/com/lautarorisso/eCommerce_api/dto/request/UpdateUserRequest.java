package com.lautarorisso.eCommerce_api.dto.request;

import jakarta.validation.constraints.Email;

public record UpdateUserRequest(
    String username,
    @Email String email) {
}
