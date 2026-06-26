package com.lautarorisso.eCommerce_api.dto.response;

import java.math.BigDecimal;

public record productDto(Long id, String name, String description, BigDecimal price, Integer stock) {
}
