package com.lautarorisso.eCommerce_api.dto.response;

import java.math.BigDecimal;
import java.util.List;

import com.lautarorisso.eCommerce_api.enums.CartStatus;

public record CartDto(Long id, List<CartItemDto> items, CartStatus status, BigDecimal total) {
}
