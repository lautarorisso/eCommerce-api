package com.lautarorisso.eCommerce_api.dto.response;

import java.math.BigDecimal;

public record cartItemDto(Long productId, String productName, Integer quantity, BigDecimal price, BigDecimal subtotal) {
}
