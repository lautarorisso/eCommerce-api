package com.lautarorisso.eCommerce_api.dto.response;

import java.math.BigDecimal;
import java.util.List;

import com.lautarorisso.eCommerce_api.enums.OrderStatus;

public record orderDto(Long id, List<orderItemDto> items, BigDecimal totalPrice, OrderStatus status) {
}
