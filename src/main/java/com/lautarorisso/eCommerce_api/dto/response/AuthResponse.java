package com.lautarorisso.eCommerce_api.dto.response;

public record AuthResponse(String token, Long userId, String email, String role) {
}
