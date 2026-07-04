package com.lautarorisso.eCommerce_api.dto.response;

import java.math.BigDecimal;
import java.util.List;

import com.lautarorisso.eCommerce_api.enums.CartStatus;

public record cartDto(Long id, List<cartItemDto> items, CartStatus status, BigDecimal total) {
}
