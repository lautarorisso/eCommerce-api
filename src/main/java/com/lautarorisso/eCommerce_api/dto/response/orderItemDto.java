package com.lautarorisso.eCommerce_api.dto.response;

import java.math.BigDecimal;

public record orderItemDto(String productName, Integer quantity, BigDecimal unitPrice, BigDecimal subtotal) {
}
