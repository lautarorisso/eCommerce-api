package com.lautarorisso.eCommerce_api.security;

public record CurrentUser(Long id, String email, String role) {
}
