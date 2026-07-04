package com.lautarorisso.eCommerce_api.dto.response;

import java.math.BigDecimal;

public record CartItemDto(Long id, Long productId, String productName, Integer quantity, BigDecimal price,
    BigDecimal subtotal) {
}
