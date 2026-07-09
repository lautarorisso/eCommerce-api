package com.lautarorisso.eCommerce_api.dto.response;

import com.lautarorisso.eCommerce_api.enums.Role;

public record UserDto(Long id, String username, String email, Role role) {
}
