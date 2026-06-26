package com.lautarorisso.eCommerce_api.dto.request;

import java.math.BigDecimal;

public record UpdateProductRequest(String name, String description, BigDecimal price) {
}
