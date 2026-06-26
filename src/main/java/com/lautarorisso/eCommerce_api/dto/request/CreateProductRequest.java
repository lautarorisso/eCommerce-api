package com.lautarorisso.eCommerce_api.dto.request;

import java.math.BigDecimal;

public record CreateProductRequest(String name, String description, BigDecimal price, Integer stock) {
}
